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
class JsonGoogleOptionParser(blackboard: ActorRef) extends BlackboardActor(blackboard) {

  val model: Model = new GoogleOptionModel

  override def receive = {
    case ContentMessage(entity) => {
      log.debug("JsonGoogleOptionParser received ContentMessage")
      JsonGoogleOptionParser.decode(entity) match {
        case Right(optionChain) => processOptionChain(optionChain)
        case Left(message) => log.warning("Decoding error: " + message)
      }
    }
    case m => super.receive(m)
  }

  def processOptionChain(optionChain: OptionChain) = {
    val chainMap = Map("expiry" -> optionChain.expiry, "expirations" -> optionChain.expirations, "underlying_id" -> optionChain.underlying_id, "underlying_price" -> optionChain.underlying_price)
    optionChain.puts map { processPut(_, chainMap)(true) }
    optionChain.puts map { processPut(_, chainMap)(false) }
  }

  def processPut(optionDetails: Map[String, String], chainDetails: Map[String, Any])(put: Boolean) = model.getKey("identifier") match {
    case Some(s) =>
      optionDetails.get(s) match {
        case Some(x) =>
          blackboard ! CandidateOption(model, x, put, optionDetails, chainDetails)
        case None => log.warning(s"logic error: details does not support key: $s")
      }
    case None => log.warning(s"logic error: model ${model.getKey("name")} does not support key: identifier")
  }
}

case class YMD(y: Int, m: Int, d: Int) {
    import com.github.nscala_time.time.Imports._
    def asDate(ymd: YMD): DateTime = ymd match {
      case YMD(y,m,d) => new DateTime(y,m,d)
    }
}

case class OptionChain(expiry: YMD, expirations: Seq[YMD], puts: Seq[Map[String, String]], calls: Seq[Map[String, String]], underlying_id: String, underlying_price: Double)

object JsonGoogleOptionParser {
  import spray.json.DefaultJsonProtocol
  import spray.httpx.unmarshalling._
  import spray.httpx.marshalling._
  import spray.httpx.SprayJsonSupport._
  import spray.json._

  object MyJsonProtocol extends DefaultJsonProtocol with NullOptions {
    implicit val ymdFormat = jsonFormat3(YMD)
    implicit val optionChainFormat = jsonFormat6(OptionChain)
  }

  import MyJsonProtocol._

  /**
   * This version of decode is a little more complex than usual because the Google
   * interface doesn't wrap attribute names in double quotes.
   * @param entity
   * @return
   */
  def decode(entity: HttpEntity): Deserialized[OptionChain] = {
    val contentType = ContentType(MediaTypes.`application/json`, HttpCharsets.`UTF-8`)
    entity match {
      case HttpEntity.NonEmpty(`contentType`, y) =>
        HttpEntity(contentType, fix(y)).as[OptionChain]
      case HttpEntity.NonEmpty(s, y) =>
        Left(MalformedContent(s"entity content type: $s"))
      case _ => Left(MalformedContent("logic error"))
    }
  }

  def fix(data: HttpData): Array[Byte] = fix(data.asString).getBytes

  def fix(s: String): String = """([^,{:\s]+):""".r.replaceAllIn(s, """"$1":""")

}