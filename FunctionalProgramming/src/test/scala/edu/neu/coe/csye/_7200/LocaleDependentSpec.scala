package edu.neu.coe.csye._7200

import org.scalatest.{ FlatSpec, Matchers }
import edu.neu.coe.csye._7200.list._
import java.util.Date
import scaladate.ScalaDate

/**
 * @author scalaprof
 */
class LocaleDependentSpec extends FlatSpec with Matchers {
  
  "today" should "equal today" in {
    val x = ScalaDate.apply(new Date(2015-1900,9,1))
    x.toString shouldBe "1 octobre 2015"
  }
  
}