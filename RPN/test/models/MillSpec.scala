package models

import org.scalatest.{ FlatSpec, Matchers }
import scala.util._

/**
 * @author scalaprof
 */
class MillSpec extends FlatSpec with Matchers {
  
  implicit val conv: String=>Try[Double] = {s => Try(s.toDouble)}
  implicit val lookup: String=>Option[Double] = DoubleMill.constants.get _
  implicit val parser = new ExpressionParser[Double](conv,lookup)
  implicit val n = implicitly[Numeric[Double]]
  
  "Mill(x)" should "evaluate 1 as 1.0" in {
    val one = Number.apply("1")(conv)
    DoubleMill().apply(one) should matchPattern { case Success(1.0) => }
  }
  it should "evaluate 1 1 + as 2.0" in {
    val result = DoubleMill().parse("1 1 plus")
    result should matchPattern { case Success(2.0) => }
  }
  it should "evaluate 3 4 * as 12.0" in {
    val result = DoubleMill().parse("3 4 *")
    result should matchPattern { case Success(12.0) => }
  }
  it should "evaluate 3 4 - as -1.0" in {
    val result = DoubleMill().parse("3 4 -")
    println(result)
    result should matchPattern { case Success(-1.0) => }
  }
  it should "evaluate 3 4 / 4 * as 3.0" in {
    val result = DoubleMill().parse("3 4 / 4 *")
    println(result)
    result should matchPattern { case Success(3.0) => }
  }
  it should "evaluate _pi as 3.14..." in {
    val result = DoubleMill().parse("_pi")
    result should matchPattern { case Success(math.Pi) => }
  }

  "Mill with repeated apply" should "evaluate 1+1 as 2.0" in {
    val mill= DoubleMill()
    val one = Number.apply("1")(conv)
    mill(one) should matchPattern { case Success(1.0) => }
    mill(one) should matchPattern { case Success(1.0) => }
    val r = mill(Operator("plus"))
    r should matchPattern { case Success(2.0) => }
  }
  it should "swap properly" in {
    val mill= DoubleMill()
    val two = Number.apply("2")(conv)
    val three = Number.apply("2")(conv)
    mill(two)
    mill(three)
    mill(Operator("swap")) should matchPattern { case Success(2.0) => }
  }
}
