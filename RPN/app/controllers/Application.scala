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

class Application extends Controller {
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  implicit val system = ActorSystem("RPN-Calculator")    
  implicit val timeout: Timeout = Timeout(10 seconds)
    
  val calculator = system.actorOf(Props.create(classOf[Calculator]))

  def index = Action.async {
    val f = (calculator ? View).mapTo[Seq[Double]]
    f map {
      case r => Ok(s"ScalaRPN: calculator has the following elements (starting with top): $r")
    }
  }

  def command(s: String) = Action.async {
    val f = (calculator ? s).mapTo[Try[Double]] 
    f map {
      case Success(r) => Ok(s"""ScalaRPN: you entered "$s" and got back $r""")
      case Failure(e) => if (s=="clr") Ok("ScalaRPN: cleared") else Ok(s"""ScalaRPN: you entered "$s" which caused error: $e""")
//      case Failure(e) => if (s=="clr") redirect("/") else  Ok(s"""ScalaRPN: you entered "$s" which caused error: $e""")
    }
  }

}
