package edu.neu.coe.scala

import org.scalatest.{ FlatSpec, Matchers }
import scala.util._
import edu.neu.coe.scala.trial._

class TrialSpec extends FlatSpec with Matchers {
  "First" should """transform "2" into 2""" in {
    val trial: Trial[String,Int] = Identity() :| {x: String => Try(x.toInt)}
    trial("2") should matchPattern { case Success(2) => }
  }
  "First with :|" should """transform "2" into 2 and "2.0" into 2.0""" in {
    val trial = First[String,Any]{x => Try(x.toInt)} :| {x: String => Try(x.toDouble)}
    trial("2") should matchPattern { case Success(2) => }
    trial("2.0") should matchPattern { case Success(2.0) => }
  }
  "First with :| and :|" should """transform "2" into 2, "2.0" into 2.0, and "true" into true""" in {
    val trial = First[String,Any]{x => Try(x.toInt)} :| {x: String => Try(x.toDouble)} :| {x: String => Try(x.toBoolean)}
    trial("2") should matchPattern { case Success(2) => }
    trial("2.0") should matchPattern { case Success(2.0) => }
    trial("true") should matchPattern { case Success(true) => }
  }
  "Identity with :||" should """transform "2" into 2, "2.0" into 2.0, and "true" into true""" in {
    val trial = Identity[String,Any]() :|| Seq({x: String => Try(x.toInt)}, {x: String => Try(x.toDouble)}, {x: String => Try(x.toBoolean)})
    trial("2") should matchPattern { case Success(2) => }
    trial("2.0") should matchPattern { case Success(2.0) => }
    trial("true") should matchPattern { case Success(true) => }
  }
  it should """transform anything into 1""" in {
    val trial = Identity[String,Any]() :|| Seq({x: String => Try(1)}, {x: String => Try(2)}, {x: String => Try(3)})
    trial("") should matchPattern { case Success(1) => }
  }
  "CurriedTrial" should "convert 2 into 5" in {
    def addIntToString(x: Int)(s: String): Try[Int] = Try(x+s.toInt)
    val three = 3
    val trial: String=>Try[Int] = CurriedTrial[Int,String,Int](addIntToString)(three)
    trial("2") should matchPattern { case Success(5) => }
  }
  "CurriedSequence" should "transform anything into 1" in {
    def success(n: Int)(s: String): Try[Int] = Success(n)
    val gs = Seq(success _, success _, success _)
    val ws = Seq(1, 2, 3)
    val trial: String=>Try[Int] = CurriedSequence[Int,Int,String,Int](gs)(ws)
    trial("") should matchPattern { case Success(1) => }
  }
//  it should "transform anything into 1 (part two)" in {
//    def success1(n: Int)(s: String): Try[Int] = Success(n)
//    def success2(n: String)(s: String): Try[Int] = Try(n.toInt)
//    val gs = Seq(success1 _, success2 _)
//    val ws = Seq(1, "2")
//    val trial: String=>Try[Int] = CurriedSequence(gs)(ws)
//    trial("") should matchPattern { case Success(1) => }
//  }
  "Identity" should """fail appropriately, regardless of input""" in {
    val trial = Identity[String,Int]()
    trial("2") should matchPattern { case Failure(TrialException("identity")) => }
  }
  it should "combine with trial function to be equivalent of First" in {
    val trial = Identity[String,Int]() :| {x: String => Try(x.toInt)}
    trial("2") should matchPattern { case Success(2) => }
  }
}