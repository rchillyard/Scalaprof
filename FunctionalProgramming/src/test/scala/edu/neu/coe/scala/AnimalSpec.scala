package edu.neu.coe.scala
package animal

import org.scalatest.{ FlatSpec, Matchers }

/**
 * @author scalaprof
 */
class AnimalSpec extends FlatSpec with Matchers {

  "Bo" should "be stripped when groomed" in {
    val bo = CairnTerrier("Bo")
    bo.stripped shouldBe false
    val grooming = new Stripping()
    val groomed = grooming.apply(bo)
    groomed.name should equal ("Bo")
    groomed.stripped shouldBe true
  }
}