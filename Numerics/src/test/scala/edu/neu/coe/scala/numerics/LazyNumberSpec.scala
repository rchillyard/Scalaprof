package edu.neu.coe.scala.numerics

import org.scalatest.{ FlatSpec, Matchers }

/**
 * @author scalaprof
 */
class LazyNumberSpec extends FlatSpec with Matchers {
  
  val one = LazyRational(1)
  val two = LazyRational(1,Product(2))
  val square = Named[Rational]("square",{x=>x*x})
  val four = two map square

	"one" should "be 1" in {
		one.get shouldBe (Rational.one)
	}
  
  it should "be -1 after negate" in {
    (one.unary_-:).get shouldBe (Rational.one.unary_-:)
  }
  
  it should "be 0 after minus(1)" in {
    (one.-(one)).get shouldBe (Rational.zero)
  }
  
  "two" should "be 2" in {
    two.get shouldBe (Rational.one+Rational.one)
  }
  
  it should "be 4 when multiplied by itself" in {
    (two * two).get shouldBe (Rational(4))
  }

  it should "be 1 when divided by itself" in {
    (two / two).get shouldBe (Rational.one)
  }

  it should "be 3 when added to one" in {
    (two + one).get shouldBe (Rational(3))
  }

  it should "be 6 when added to one and three" in {
    (two + one + LazyRational(3)).get shouldBe (Rational(6))
  }

  it should "be 3 when added to one by explicit function" in {
    val lr = two map Named("add Rat.1",{ x => x+Rational.one })
    lr.get shouldBe (Rational.one*3)
  }
  
  "for comprehension" should "give 4" in {
    val z = for (x <- two ) yield square(x)
    z.get should be (Rational(4))
  }

  it should "give 8" in {
    val z = for (x <- two; y <- four ) yield x*y
    z.get should be (Rational(8))
  }

//  it should "give NoFunction" in {
//    val z = for (x <- two; if(x==Rational(1)); y <- four ) yield x*y
//    z.f should be (NoFunction())
//  }


}