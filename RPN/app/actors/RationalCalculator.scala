package actors

import akka.actor.{ Actor, ActorLogging, ActorRef }
import scala.util._
import models._
import edu.neu.coe.scala.numerics.Rational

/**
 * @author scalaprof
 *
 */
class RationalCalculator extends Actor with ActorLogging {
  
  implicit val conv: String=>Try[Rational] = {s => Try(Rational(s))}
  implicit val lookup: String=>Option[Rational] = RationalMill.constants.get _
  implicit val parser = new ExpressionParser[Rational]
  val mill: Mill[Rational] = RationalMill()
  
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
