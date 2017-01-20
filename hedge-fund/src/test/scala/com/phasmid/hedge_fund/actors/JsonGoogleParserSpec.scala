package com.phasmid.hedge_fund.actors

import akka.actor.{ ActorSystem, Actor, Props, ActorRef }
import akka.testkit._
import org.scalatest.{ WordSpecLike, Matchers, BeforeAndAfterAll }
import scala.io.Source
import scala.concurrent.duration._
import spray.http._
import spray.http.MediaTypes._
import org.scalatest.Inside
import scala.language.postfixOps
import spray.json.pimpString

/**
 * This specification really tests much of the HedgeFund app but because it particularly deals with
 * processing data from the YQL (Yahoo Query Language) using JSON, we call it by its given name.
 */
class JsonGoogleParserSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
    with WordSpecLike with Matchers with Inside with BeforeAndAfterAll {

  def this() = this(ActorSystem("JsonGoogleParserSpec"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  import scala.language.postfixOps
  val json = Source.fromFile("src/test/resources/googleExample.json") mkString

  "json read" in {
    import spray.json._
    val obj = JsonGoogleParser.fix(json).parseJson
  }

  "json conversion" in {
    val contentTypeText = ContentType(MediaTypes.`text/html`, HttpCharsets.`ISO-8859-1`)
    val entity = HttpEntity(contentTypeText, json.getBytes())
    val ok = JsonGoogleParser.decode(entity) match {
      case Right(x) =>
        x.seq.length should equal(2)
        val quotes = x.seq
        quotes(0).get("t") should matchPattern { case Some(Some("AAPL")) => }

      case Left(x) =>
        fail("decoding error: " + x)
    }
  }

  "send back" in {
    val blackboard = system.actorOf(Props.create(classOf[MockGoogleBlackboard], testActor), "blackboard")
    val contentType = ContentType(MediaTypes.`text/html`, HttpCharsets.`ISO-8859-1`)
    val entityParser = _system.actorOf(Props.create(classOf[EntityParser], blackboard), "entityParser")
    val entity = HttpEntity(contentType, json.getBytes())
    entityParser ! EntityMessage("json:GF", entity)
    val msg = expectMsgClass(3.seconds, classOf[QueryResponse])
    println("msg received: " + msg)
    msg should matchPattern {
      case QueryResponseValid("AAPL", m) =>
    }
    inside(msg) {
      case QueryResponseValid(symbol, attributes) => attributes.get("l") should matchPattern { case Some("124.50") => }
    }
  }
}

import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Await
import com.phasmid.hedge_fund.model.Model

class MockGoogleUpdateLogger(blackboard: ActorRef) extends UpdateLogger(blackboard) {
  override def processStock(identifier: String, model: Model) = {
    model.getKey("price") match {
      case Some(p) => {
        // sender is the MarketData actor
        val future = (sender ? SymbolQuery(identifier, List(p))).mapTo[QueryResponse]
        val result = Await.result(future, timeout.duration)
        result match {
          case QueryResponseValid(k,a) =>
            a map {
              case (k, v) =>
                log.info(s"$identifier attribute $k has been updated to: $v")
                blackboard ! result
            }
          case _ => 
        }
      }
      case None => log.warning(s"'price' not defined in model")
    }
  }
}

class MockGoogleBlackboard(testActor: ActorRef) extends Blackboard(Map(classOf[KnowledgeUpdate] -> "marketData", classOf[SymbolQuery] -> "marketData", classOf[OptionQuery] -> "marketData", classOf[CandidateOption] -> "optionAnalyzer", classOf[Confirmation] -> "updateLogger"),
  Map("marketData" -> classOf[MarketData], "optionAnalyzer" -> classOf[OptionAnalyzer], "updateLogger" -> classOf[MockGoogleUpdateLogger])) {

  override def receive =
    {
      case msg: Confirmation => msg match {
        // Cut down on the volume of messages
        case Confirmation("AAPL", _, _) => super.receive(msg)
        case _ =>
      }
      case msg: QueryResponseValid => testActor forward msg

      case msg => super.receive(msg)
    }
}

