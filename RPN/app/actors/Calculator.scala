package actors

import akka.actor.{ Actor, ActorLogging, ActorRef }
import scala.util._
import models._

/**
 * @author scalaprof
 *
 * CONSIDER making parser implicit
 */
class Calculator[A : Numeric](mill: Mill[A], parser: ExpressionParser[A]) extends Actor with ActorLogging {
  
  override def receive = {
    case View => sender ! mill.toSeq
    case x: String =>
      log.info(s"received $x")
      try {
        val response = mill.parse(x)(parser)
        log.info(s"response: $response")
        sender ! response
      }
      catch {
        case t: Throwable => println("should never hit this line"); log.error(t, "logic error: should never log this issue")
      }
    case z =>
      log.warning(s"received unknown message type: $z")
  }
}

object View