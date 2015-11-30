package models

import org.scalatest.{ FlatSpec, Matchers }
import scala.util._


/**
 * @author scalaprof
 */
class ExpressionParserSpec extends FlatSpec with Matchers {
  implicit val conv: String=>Try[Double] = {s => Try(s.toDouble)}
  implicit val lookup: String=>Option[Double] = DoubleMill.constants.get _
    val parser = new ExpressionParser[Double](conv,lookup)
  "parser.value" should "parse 1 as Number" in {
    val r = parser.parseAll(parser.value, "1")
    r should matchPattern { case parser.Success(_, _) => }
    r.get should matchPattern { case Number("1") => }
  }
  it should "parse 1.0 as Number" in {
    val r = parser.parseAll(parser.value, "1.0")
    r should matchPattern { case parser.Success(_, _) => }
    r.get should matchPattern { case Number("1.0") => }
  }
  "parser.op" should "parse + as Operator" in {
    val r = parser.parseAll(parser.op, "+")
    r should matchPattern { case parser.Success(_, _) => }
    r.get should matchPattern { case Operator("+") => }
  }
  it should "parse - as Operator" in {
    val r = parser.parseAll(parser.op, "-")
    r should matchPattern { case parser.Success(_, _) => }
    r.get should matchPattern { case Operator("-") => }
  }
  it should "parse * as Operator" in {
    val r = parser.parseAll(parser.op, "*")
    r should matchPattern { case parser.Success(_, _) => }
    r.get should matchPattern { case Operator("*") => }
  }
  it should "parse / as Operator" in {
    val r = parser.parseAll(parser.op, "/")
    r should matchPattern { case parser.Success(_, _) => }
    r.get should matchPattern { case Operator("/") => }
  }
  "parser.meminst" should "parse sto:k as meminst" in {
    val r = parser.parseAll(parser.meminst, "sto:k")
    r should matchPattern { case parser.Success(_, _) => }
    r.get should matchPattern { case MemInst("sto","k") => }
  }
  it should "parse rcl:k as meminst" in {
    val r = parser.parseAll(parser.meminst, "rcl:k")
    r should matchPattern { case parser.Success(_, _) => }
    r.get should matchPattern { case MemInst("rcl","k") => }
  }
  "parser.const" should "parse _e as const" in {
    val r = parser.parseAll(parser.const, "_e")
    r should matchPattern { case parser.Success(_, _) => }
    r.get should matchPattern { case Constant("e") => }
  }
  "parser.term" should "parse 1 as Number" in {
    val r = parser.parseAll(parser.term, "1")
    r should matchPattern { case parser.Success(_, _) => }
    r.get should matchPattern { case Number("1") => }
  }
  it should "parse + as Operator" in {
    val r = parser.parseAll(parser.term, "+")
    r should matchPattern { case parser.Success(_, _) => }
    r.get should matchPattern { case Operator("+") => }
  }
  it should "parse sto:k as meminst" in {
    val r = parser.parseAll(parser.term, "sto:k")
    r should matchPattern { case parser.Success(_, _) => }
    r.get should matchPattern { case MemInst("sto","k") => }
  }
  "parser.expr" should "parse 1 as 1" in {
    val r = parser.parseAll(parser.expr, "1")
    r should matchPattern { case parser.Success(_, _) => }
    r.get.head should matchPattern { case Number("1") => }
  }
  it should "parse 1 1 as 1 1" in {
    val r = parser.parseAll(parser.expr, "1 1")
    r should matchPattern { case parser.Success(_, _) => }
    r.get.head should matchPattern { case Number("1") => }
    r.get.tail.head should matchPattern { case Number("1") => }
  }
  it should "parse 1 1 + as 1 1 +" in {
    val r = parser.parseAll(parser.expr, "1 1 +")
    r should matchPattern { case parser.Success(_, _) => }
    r.get.head should matchPattern { case Number("1") => }
    r.get.tail.head should matchPattern { case Number("1") => }
    r.get.tail.tail.head should matchPattern { case Operator("+") => }
  }
}
