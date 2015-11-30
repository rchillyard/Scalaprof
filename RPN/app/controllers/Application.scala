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
import edu.neu.coe.scala.numerics.Rational
import akka.actor.ActorRef
import com.typesafe.config.{ ConfigFactory, Config }
import models._


class Application extends Controller {
  
//  val config = ConfigFactory.load()
  
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  implicit val timeout: Timeout = Timeout(10 seconds)
  implicit val system = ActorSystem("RPN-Calculator")  
  val setup = Application.getSetupForRational
  val calculator = system.actorOf(setup _1,setup _2)
 
  val prefix: String = setup _3

  def index() = Action.async {
    val xsf = (calculator ? View).mapTo[Seq[_]]
    xsf map {
      case xs => Ok(s"$prefix: calculator has the following elements (starting with top): $xs")
    }
  }

  def command(s: String) = Action.async {
    val xtf = (calculator ? s).mapTo[Try[_]] 
    xtf map {
      case Success(x) => Ok(s"""$prefix: you entered "$s" and got back $x""")
      case Failure(e) => if (s=="clr") Ok("ScalaRPN: cleared") else Ok(s"""ScalaRPN: you entered "$s" which caused error: $e""")
//      case Failure(e) => if (s=="clr") redirect("/") else  Ok(s"""ScalaRPN: you entered "$s" which caused error: $e""")
    }
  }

}

object Application {
  def getSetupForDouble(implicit system: ActorSystem) = {
		  implicit val lookup: String=>Option[Double] = DoubleMill.constants.get _
      implicit val conv: String=>Try[Double] = DoubleMill.valueOf _
			implicit val parser = new ExpressionParser[Double](conv,lookup)
      println(s"creating double calculator with parser: ${parser.toString()}")
			val mill: Mill[Double] = DoubleMill()
			// Note: the following pattern should NOT be used within an actor
      val props = Props(new Calculator(mill,parser))
			(props,"doubleCalculator","Double Calculator")
  }
    def getSetupForRational(implicit system: ActorSystem) = {
      implicit val lookup: String=>Option[Rational] = RationalMill.constants.get _
      implicit val conv: String=>Try[Rational] = RationalMill.valueOf _
      implicit val parser = new ExpressionParser[Rational](conv,lookup)
      println(s"creating double calculator with parser: ${parser.toString()}")
      val mill: Mill[Rational] = RationalMill()
      // Note: the following pattern should NOT be used within an actor
      val props = Props(new Calculator(mill,parser))
      (props,"rationalCalculator","Rational Calculator")
  }
}
