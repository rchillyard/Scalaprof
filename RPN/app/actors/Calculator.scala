package actors

import akka.actor.{ Actor, ActorLogging, ActorRef }
import scala.util._
import models._

/**
 * @author scalaprof
 *
 */
class Calculator extends Actor with ActorLogging {
  
  implicit val conv: String=>Try[Double] = {s => Try(s.toDouble)}
  implicit val lookup: String=>Option[Double] = DoubleMill.constants.get _
  implicit val parser = new ExpressionParser[Double]
  val mill: Mill[Double] = DoubleMill()
  
  override def receive = {
    case View => sender ! mill.toSeq
    case x: String =>
      log.info(s"received $x")
      try {
        sender ! mill.parse(x)
      }
      catch {
        case t: Throwable => println("should never hit this line"); log.error(t, "logic error: should never log this issue")
      }
    case z =>
      log.warning(s"received unknown message type: $z")
  }
}

object View