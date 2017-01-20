package com.phasmid.hedge_fund.rules

import org.scalatest.{ FlatSpec, Matchers }
import scala.util._

/**
 * @author scalaprof
 */
class RuleParserSpec extends FlatSpec with Matchers {

  val debuggerOff = { x: String => () }
  val debuggerOn = { x: String => println(x) }

  "RuleParse.factor" should "parse 1 as 1.0" in {
    val parser = new RuleParser
    val r = parser.parseAll(parser.factor, "1")
    r should matchPattern { case parser.Success(_, _) => }
    r.get.value should matchPattern { case Success(1.0) => }
  }
  it should "parse 1.0K as 1000.0" in {
    val parser = new RuleParser
    val r = parser.parseAll(parser.factor, "1.0K")
    r should matchPattern { case parser.Success(_, _) => }
    r.get.value should matchPattern { case Success(1000.0) => }
  }
  it should "parse $x as 1.0" in {
    val parser = new RuleParser({case "x"=>Some(1)},debuggerOff)
    val r = parser.parseAll(parser.factor, "$x")
    r should matchPattern { case parser.Success(_, _) => }
    r.get.value should matchPattern { case Success(1.0) => }
  }
  it should "parse 1.0 as 1.0" in {
    val parser = new RuleParser({case "x"=>Some(1)},debuggerOff)
    val r = parser.parseAll(parser.factor, "1.0")
    r should matchPattern { case parser.Success(_, _) => }
    r.get.value should matchPattern { case Success(1.0) => }
  }
  it should "parse $x as 1000.0" in {
    val parser = new RuleParser({case "x"=>Some("1.0K")},debuggerOff)
    val r = parser.parseAll(parser.factor, "$x")
    r should matchPattern { case parser.Success(_, _) => }
    r.get.value should matchPattern { case Success(1000.0) => }
  }
  "RuleParse.factorTerm" should "parse $x*2 as 1.0" in {
    val parser = new RuleParser({case "x"=>Some(1)},debuggerOff)
    val r = parser.parseAll(parser.factorTerm, "$x*2")
    r should matchPattern { case parser.Success(_, _) => }
    r.get.value should matchPattern { case Success(2.0) => }
  }
  "RuleParse.factorExpr" should "parse $x*2+1 as 1.0" in {
    val parser = new RuleParser({case "x"=>Some(1)},debuggerOff)
    val r = parser.parseAll(parser.factorExpr, "$x*2+1")
    r should matchPattern { case parser.Success(_, _) => }
    r.get.value should matchPattern { case Success(3.0) => }
  }
  "RuleParse.parseRule" should "parse 1=1 as true" in {
    (new RuleParser).parseRule("1=1") should matchPattern { case Success(true) => }
  }
  it should "parse 1.0=1.0 as true" in {
    (new RuleParser).parseRule("1.0=1.0") should matchPattern { case Success(true) => }
  }
  it should "parse 1.0=1.0000001 as true" in {
    (new RuleParser).parseRule("1.0=1.0000001") should matchPattern { case Success(true) => }
  }
  it should "parse 1.0=1.000001 as false" in {
    (new RuleParser).parseRule("1.0=1.000001") should matchPattern { case Success(false) => }
  }
  it should "parse 1>0 as true" in {
    (new RuleParser).parseRule("1>0") should matchPattern { case Success(true) => }
  }
  it should "parse 1>0|1<0 as true" in {
    (new RuleParser).parseRule("1 > 0 | 1 < 0") should matchPattern { case Success(true) => }
  }
  it should "parse 1>0 & 1<0 as false" in {
    (new RuleParser).parseRule("1>0 & 1<0") should matchPattern { case Success(false) => }
  }
  it should "parse 1>0 & (1<0) as false" in {
    (new RuleParser).parseRule("1>0 & (1<0)") should matchPattern { case Success(false) => }
  }
  it should "parse $x>0 as true" in {
    val parser = new RuleParser({case "x"=>Some(1)},debuggerOff)
    parser.parseRule("$x>0") should matchPattern { case Success(true) => }
  }
  it should "parse $x<0 as false" in {
    val parser = new RuleParser({case "x"=>Some(1)},debuggerOff)
    parser.parseRule("$x<0") should matchPattern { case Success(false) => }
  }
  it should "parse $x>$y as true" in {
    val parser = new RuleParser({case "x"=>Some(1); case "y"=>Some(0.5)},debuggerOn)
    parser.parseRule("$x>$y") should matchPattern { case Success(true) => }
  }
  it should "parse $x<$y as false" in {
    val parser = new RuleParser({case "x"=>Some(1); case "y"=>Some(0.5)},debuggerOff)
    parser.parseRule("$x<$y") should matchPattern { case Success(false) => }
  }
  it should "parse $x<$y as false with debugger" in {
    val parser = new RuleParser({case "x"=>Some(1); case "y"=>Some(0.5)},debuggerOn)
    parser.parseRule("$x<$y") should matchPattern { case Success(false) => }
  }
  it should "parse $x=$y*2 as true" in {
    val parser = new RuleParser({case "x"=>Some(1); case "y"=>Some(0.5)},debuggerOff)
    parser.parseRule("$x=$y*2") should matchPattern { case Success(true) => }
  }
  it should "parse $x!=$y*2 as false" in {
    val parser = new RuleParser({case "x"=>Some(1); case "y"=>Some(0.5)},debuggerOn)
    parser.parseRule("$x!=$y*2") should matchPattern { case Success(false) => }
  }
  it should "parse Always as true" in {
    (new RuleParser).parseRule("Always") should matchPattern { case Success(true) => }
  }
  it should "parse Never as false" in {
    (new RuleParser).parseRule("Never") should matchPattern { case Success(false) => }
  }
//  "(" should "fail" in {
//    val parser = new RuleParse
//    val r = parser.parseAll(parser.expr, "(")
//    r should matchPattern { case parser.Failure("factor", _) => }
//  }
//  "1+2=2" should "fail" in {
//    val parser = new RuleParse
//    val r = parser.parseAll(parser.expr, "1+2=2")
//    r should matchPattern { case parser.Failure("expr", _) => }
//  }
}
