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

class Application extends Controller {
  
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  implicit val system = ActorSystem("RPN-Calculator")    
  implicit val timeout: Timeout = Timeout(10 seconds) 
  val calculator = system.actorOf(Props.create(classOf[Calculator]))


  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

    def command(s: String) = Action.async {
      val f = (calculator ? s).mapTo[Double]
      f map {x => Ok(views.html.index(s"The answer is: $x."))}
  }

}
