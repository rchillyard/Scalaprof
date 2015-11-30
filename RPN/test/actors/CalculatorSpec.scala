package actors

import org.scalatest._
import akka.testkit.TestActorRef
import scala.concurrent.duration._
import scala.concurrent.Await
import akka.pattern.ask
import scala.util._
import scala.io.Source
import scala.concurrent._
import scala.concurrent.duration._
import com.typesafe.config.{ ConfigFactory, Config }
import akka.actor.{ Actor, ActorSystem, Props, ActorRef }
import akka.util.Timeout
import java.net.URL
import org.scalatest.concurrent._
import org.scalatest._
import org.scalatest.time._
import edu.neu.coe.scala.numerics.Rational
import models._


class CalculatorSpec extends FlatSpec with Matchers with Futures with ScalaFutures with Inside {
  implicit val system = ActorSystem("CountWords")  
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  implicit val timeout: Timeout = Timeout(10 seconds)

  "Rational Calculator" should "yield empty list for /" in {
      val lookup: String=>Option[Rational] = RationalMill.constants.get _
      val conv: String=>Try[Rational] = RationalMill.valueOf _
      val parser = new ExpressionParser[Rational](conv,lookup)
      val mill: Mill[Rational] = RationalMill()
      val props = Props(new Calculator(mill,parser))
      val taf = TestActorRef(props)
      val xsf = (taf ? View).mapTo[Seq[Rational]]
      val nf = xsf map { case xs => xs.size }
      whenReady(nf, timeout(Span(6, Seconds))) { case 0 => }
  }
  it should "yield 1 for 1" in {
      val lookup: String=>Option[Rational] = RationalMill.constants.get _
      val conv: String=>Try[Rational] = RationalMill.valueOf _
      val parser = new ExpressionParser[Rational](conv,lookup)
      val mill: Mill[Rational] = RationalMill()
      val props = Props(new Calculator(mill,parser))
      val taf = TestActorRef(props)
      val xtf = (taf ? "1").mapTo[Try[Rational]]
      whenReady(xtf, timeout(Span(6, Seconds))) { case Success(Rational(1,1)) => }
  }
  it should "yield 1 when given floating point problem" in {
      val lookup: String=>Option[Rational] = RationalMill.constants.get _
      val conv: String=>Try[Rational] = RationalMill.valueOf _
      val parser = new ExpressionParser[Rational](conv,lookup)
      val mill: Mill[Rational] = RationalMill()
      val props = Props(new Calculator(mill,parser))
      val taf = TestActorRef(props)
      val xtf = (taf ? "0.2 0.1 + 10 * 3 /").mapTo[Try[Rational]]
      whenReady(xtf, timeout(Span(6, Seconds))) { case Success(Rational(1,1)) => }
  }
  "Double Calculator" should "yield empty list for /" in {
      val lookup: String=>Option[Double] = DoubleMill.constants.get _
      val conv: String=>Try[Double] = DoubleMill.valueOf _
      val parser = new ExpressionParser[Double](conv,lookup)
      val mill: Mill[Double] = DoubleMill()
      val props = Props(new Calculator(mill,parser))
      val taf = TestActorRef(props)
      val xsf = (taf ? View).mapTo[Seq[Double]]
      val nf = xsf map { case xs => xs.size }
      whenReady(nf, timeout(Span(6, Seconds))) { case 0 => }
  }
  
  // This test suffers from a very peculiar bug which might even be a bug
  // in the Scala compiler. Kudos to you if you can fix it!!
  ignore should "yield 1 for 1" in {
      val lookup: String=>Option[Double] = DoubleMill.constants.get _
      val conv: String=>Try[Double] = DoubleMill.valueOf _
      val parser = new ExpressionParser[Double](conv,lookup)
      val mill: Mill[Double] = DoubleMill()
      val props = Props(new Calculator(mill,parser))
      val taf = TestActorRef(props)
      val xtf = (taf ? "1").mapTo[Try[Double]]
      whenReady(xtf, timeout(Span(6, Seconds))) { case Success(1.0) => }
  }
}
