package controllers

import play.api._
import play.api.mvc._
import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent._
import scala.concurrent.duration._
import scala.util._
import edu.neu.coe.scala.numerics.Rational
import akka.actor.ActorRef
import com.typesafe.config.{ ConfigFactory, Config }
import actors._
import models._

class Application extends Controller {
  
  val config = ConfigFactory.load()
  val which = config.getString("calculator")
  
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  implicit val timeout: Timeout = Timeout(10 seconds)
  implicit val system = ActorSystem("RPN-Calculator")
  val setup = which match {
    case "rational" => Application.getSetupForRational
    case "double" => Application.getSetupForDouble
    case _ => Console.err.println(s"Unsupported calculator type: $which"); Application.getSetupForRational
  }
  val calculator = system.actorOf(setup _1,setup _2)
  val name: String = setup _3;
  println(s"$name is ready")

  def index() = Action.async {
    val xsf = (calculator ? View).mapTo[Seq[_]]
    xsf map {
      case xs => Ok(s"$name: calculator has the following elements (starting with top): $xs")
    }
  }

  def command(s: String) = Action.async {
    val xtf = (calculator ? s).mapTo[Try[_]] 
    xtf map {
      case Success(x) => Ok(s"""$name: you entered "$s" and got back $x""")
      case Failure(e) => if (s=="clr") Ok("$name: cleared") else Ok(s"""$name: you entered "$s" which caused error: $e""")
//      case Failure(e) => if (s=="clr") redirect("/") else  Ok(s"""$name: you entered "$s" which caused error: $e""")
    }
  }

}

object Application {
  def getSetupForDouble(implicit system: ActorSystem) = {
		  implicit val lookup: String=>Option[Double] = DoubleMill.constants.get _
      implicit val conv: String=>Try[Double] = DoubleMill.valueOf _
			implicit val parser = new ExpressionParser[Double](conv,lookup)
			val mill: Mill[Double] = DoubleMill()
			// Note: the following pattern should NOT be used within an actor
      val props = Props(new Calculator(mill,parser))
			(props,"doubleCalculator","Double Calculator")
  }
  // CONSIDER This assumes that we have Rational in our classpath already.
  // I'd like to try the possibility of dynamically loading the Rational stuff.
  // But, that's going to be very tricky, so we'll leave it for now.
    def getSetupForRational(implicit system: ActorSystem) = {
      implicit val lookup: String=>Option[Rational] = RationalMill.constants.get _
      implicit val conv: String=>Try[Rational] = RationalMill.valueOf _
      implicit val parser = new ExpressionParser[Rational](conv,lookup)
      val mill: Mill[Rational] = RationalMill()
      // Note: the following pattern should NOT be used within an actor
      val props = Props(new Calculator(mill,parser))
      (props,"rationalCalculator","Rational Calculator")
  }
}
