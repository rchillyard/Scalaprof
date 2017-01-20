package com.phasmid.hedge_fund.actors

import akka.actor.{ ActorRef, Props }
import spray.http._
import scala.util._
import com.phasmid.hedge_fund.model._

/**
 * TODO create a super-type for this kind of actor
 *
 * @author robinhillyard
 */
class JsonYQLParser(blackboard: ActorRef) extends BlackboardActor(blackboard) {

  val model: Model = new YQLModel

  override def receive = {
    case ContentMessage(entity) => {
      log.debug("JsonYQLParser received ContentMessage")
      JsonYQLParser.decode(entity) match {
        case Right(response) => processQuote(response.query.results.quote)
        case Left(message) => log.warning(message.toString())
      }
    }
    case m => super.receive(m)
  }

  def processQuote(quotes: Seq[Map[String, Option[String]]]) = quotes foreach { q => processInstrument(q) }

  def processInstrument(quote: Map[String, Option[String]]) = model.getKey("symbol") match {
    case Some(s) =>
      quote.get(s) match {
        case Some(Some(symbol)) => updateMarket(symbol, quote)
        case _ => log.warning(s"symbol $s is undefined")
      }
    case _ => log.warning("'symbol' is undefined in model")
  }

  def updateMarket(symbol: String, quote: Map[String, Option[String]]) = blackboard ! KnowledgeUpdate(model, symbol, quote flatMap { case (k, Some(v)) => Option(k -> v); case _ => None })
}

object JsonYQLParser {
  import spray.json.DefaultJsonProtocol
  import spray.httpx.unmarshalling._
  import spray.httpx.marshalling._
  import spray.httpx.SprayJsonSupport._
  import spray.json._

  case class Response(query: Query)
  case class Query(count: Int, created: String, lang: String, diagnostics: Option[Diagnostics], results: Results)
  case class Diagnostics(url: Seq[Map[String, String]], publiclyCallable: String, `user-time`: String, `service-time`: String, `build-version`: String, query: DiagnosticsQuery,
    cache: DiagnosticsCache, javascript: DiagnosticsJavascript)
  case class DiagnosticsQuery(`execution-start-time`: String, `execution-stop-time`: String, `execution-time`: String, params: String, content: String)
  case class DiagnosticsCache(`execution-start-time`: String, `execution-stop-time`: String, `execution-time`: String, method: String, `type`: String, content: String)
  case class DiagnosticsJavascript(`execution-start-time`: String, `execution-stop-time`: String, `execution-time`: String, `instructions-used`: String, `table-name`: String)
  case class Results(quote: Seq[Map[String, Option[String]]]) {
    def get(index: Int, key: String): Option[String] = {
      Try { quote(index) } match {
        case Success(y) => y.get(key) match { case Some(x) => x; case None => None }
        case Failure(y) => None
      }
    }
  }

  object MyJsonProtocol extends DefaultJsonProtocol with NullOptions {
    implicit val diagnosticsQueryFormat = jsonFormat5(DiagnosticsQuery)
    implicit val diagnosticsCacheFormat = jsonFormat6(DiagnosticsCache)
    implicit val diagnosticsJavascriptFormat = jsonFormat5(DiagnosticsJavascript)
    implicit val diagnosticsFormat = jsonFormat8(Diagnostics)
    implicit val resultsFormat = jsonFormat1(Results)
    implicit val queryFormat = jsonFormat5(Query)
    implicit val entityFormat = jsonFormat1(Response)
  }

  import MyJsonProtocol._

  def decode(entity: HttpEntity) = entity.as[Response]

}