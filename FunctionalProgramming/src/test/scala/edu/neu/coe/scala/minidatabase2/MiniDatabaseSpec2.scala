package edu.neu.coe.scala.minidatabase2

import org.scalatest.{ FlatSpec, Matchers, Inside }
import scala.util._

/**
 * @author scalaprof
 */
class MiniDatabase2Spec extends FlatSpec with Inside with Matchers {

  "Height" should "succeed 6 ft 5 in" in {
    Height.parse("6 ft 5 in") should matchPattern { case Success(h) => }
  }
  it should "fail 6 ft five in" in {
    Height.parse("6 ft five in") should matchPattern { case Failure(x) => }
  }
  it should """succeed 6' 5"""" in {
    Height.parse("""6' 5"""") should matchPattern { case Success(h) => }
  }
  it should """fail to parse 6'""" in {
    Height.parse("""6'""") should matchPattern { case Failure(x) => }
  }
  it should "succeed: equal 77 inches and be considered tall" in {
    val height = Height.parse("6 ft 5 in")    
    inside(height) {
      case Success(h) => h should matchPattern { case Height(6,5) => }
    }
    inside(height) {
      case Success(h) => h.inches shouldEqual (77)
    }
    inside(height) {
      case Success(h) => MiniDatabase2.measure(h) should be ("tall")
    }
  }

  "Name" should "succeed: Tom Brady" in {
    Name.parse("Tom Brady") should matchPattern { case Success(h) => }
  }
  
  it should """succeed: Thomas E. P. "Tom" Brady""" in {
    Name.parse("""Thomas E. P. "Tom" Brady""") should matchPattern { case Success(h) => }
  }
  
  "Entry" should """succeed: Thomas E. P. "Tom" Brady, etc.""" in {
    Entry.parse("""Thomas E. P. "Tom" Brady, 078-05-1120, Aug 3rd 1977, 6 ft 4 in, 225""".split(",")) should matchPattern { case Success(h) => }
  }
  
  it should """fail: Thomas E. P. "Tom" Brady""" in {
    Entry.parse("""Brady, 123-45-6789""".split(",")) should matchPattern { case Failure(x) => }
  }
}