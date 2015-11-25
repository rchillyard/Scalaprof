package actors

import akka.actor.Actor
import models.ExpressionParser
import scala.collection.mutable.Stack
import models.Mill
import akka.actor.ActorLogging


/**
 * @author scalaprof
 */
class Calculator extends Actor with ActorLogging {
  
  val mill = Mill(Stack[Double](), new ExpressionParser)
  
  override def receive = {
    case x: String => sender ! mill(x)
    case _ => log.warning("bad message")
  }
}