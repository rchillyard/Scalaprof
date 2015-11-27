package controllers

import play.api._
import play.api.mvc._
import actors.Calculator
import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent._
import scala.concurrent.duration._
import scala.util._
import actors.View
import actors.RationalCalculator
import edu.neu.coe.scala.numerics.Rational
import akka.actor.ActorRef

abstract class ApplicationBase[A : Numeric] extends Controller {
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  implicit val timeout: Timeout = Timeout(10 seconds)
  
  def index: Action[AnyContent]
  def command(s: String): Action[AnyContent]

  def index(actor: ActorRef, prefix: String) = Action.async {
    val rsf = (actor ? View).mapTo[Seq[A]]
    rsf map {
      case rs => Ok(s"$prefix: calculator has the following elements (starting with top): $rs")
    }
  }

  def command(actor: ActorRef, s: String, prefix: String) = Action.async {
    val rtf = (actor ? s).mapTo[Try[A]] 
    rtf map {
      case Success(r) => Ok(s"""$prefix: you entered "$s" and got back $r""")
      case Failure(e) => if (s=="clr") Ok("ScalaRPN: cleared") else Ok(s"""ScalaRPN: you entered "$s" which caused error: $e""")
//      case Failure(e) => if (s=="clr") redirect("/") else  Ok(s"""ScalaRPN: you entered "$s" which caused error: $e""")
    }
  }

}
