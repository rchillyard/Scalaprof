package edu.neu.coe.csye._7200

import org.scalatest.{ FlatSpec, Matchers }
import Rational.RationalHelper

/**
 * @author scalaprof
 */
class RationalSpec extends FlatSpec with Matchers {

	"0" should "be OK" in {
		Rational(0)
	}
  it should "be ZERO" in {
    Rational(0) shouldBe Rational.ZERO
  }
	it should "be whole" in {
		Rational.ZERO shouldBe 'whole
	}
	it should "be zero" in {
		Rational.ZERO shouldBe 'zero
	}
	it should "equal 0" in {
		Rational.ZERO.toInt should be (0)
	}
	it should "equal infinity when inverted" in {
		Rational.ZERO.invert shouldBe 'infinity
	}
  it should "equal BigDecimal.ZERO" in {
    Rational.ZERO.toBigDecimal shouldBe BigDecimal(0)
  }
  it should "equal r when added to r" in {
    val r = Rational(22,7) // we could choose anything here
    (Rational.ZERO+r) should be (r)
  }

  "1/2" should "be OK" in {
    Rational.HALF * 2 shouldBe Rational.ONE
  }
  it should "equal HALF" in {
    Rational("1/2") shouldBe Rational.HALF
  }
  it should "be half of ONE" in {
    Rational.HALF * 2 shouldBe Rational.ONE
  }
  it should "be OK using r-interpolator" in {
    r"1/2" * 2 shouldBe Rational.ONE
  }
  it should "be OK using r-interpolator with variable" in {
    val denom = 2
    r"1/$denom" * denom shouldBe Rational.ONE
  }
  
  "1" should "be OK" in {
    Rational(1)
  }
  it should "be ONE" in {
    Rational(1) shouldBe Rational.ONE
  }
  it should "be whole" in {
    Rational.ONE shouldBe 'whole
  }
  it should "be unity" in {
    Rational.ONE shouldBe 'unity
  }
  it should "equal 1" in {
    Rational.ONE.toInt should be (1)
  }
  it should "not equal infinity when inverted" in {
    Rational.ONE.invert should not be 'infinity
  }
  it should "equal itself when inverted" in {
    Rational.ONE.invert should be (Rational.ONE)
  }
  it should "equal BigDecimal.ONE" in {
    Rational.ONE.toBigDecimal shouldBe BigDecimal(1)
  }
  it should "equal r when multiplied by r" in {
    val r = Rational(22,7) // we could choose anything here
    (Rational.ONE*r) should be (r)
  }

	"10" should "be OK" in {
		Rational(10)
	}
  it should "be TEN" in {
    Rational(10) shouldBe Rational.TEN
  }
	it should "be whole" in {
		Rational.TEN shouldBe 'whole
	}
	it should "not be zero" in {
		Rational.TEN should not be 'zero
	}
	it should "equal 10" in {
		Rational.TEN.toInt should  be (10)
	}
  it should "equal 5*2" in {
    (Rational.TEN/2) should  be (Rational(5))
  }
  it should "equal 10*1" in {
    (Rational.TEN/10) should  be (Rational.ONE)
  }
  it should "equal BigDecimal(10)" in {
    Rational.TEN.toBigDecimal shouldBe BigDecimal(10)
  }
  it should "barf when raised to 10th power" in {
    val thrown = the [Exception] thrownBy Rational.TEN.power(10).toInt
        thrown.getMessage should equal ("Rational(10000000000,1) is too big for Int")
  }

	"2/3" should "be OK" in {
		Rational(2,3)
	}
	it should "not be whole" in {
		Rational(2,3) should not be 'whole
	}
	it should "equal 2 when multiplied by 3" in {
		(Rational(2,3)*3 toInt) should be (2)
	}
	it should "equal 3/2 when inverted" in {
		Rational(2, 3).invert should be (Rational(3,2))
	}
  it should "equal 5/3 when added to 1" in {
    (Rational.ONE+Rational(2,3)) should be (Rational(5,3))
  }
  it should "equal 4/9 when multiplied by itself" in {
    val r = Rational(2,3)
    (r*r) should be (Rational(4,9))
  }
  it should "equal 4/9 when squared" in {
    (Rational(2,3)^2) should be (Rational(4,9))
  }
  it should "barf when toInt invoked" in {
        an [IllegalArgumentException] should be thrownBy Rational(2,3).toInt

//    val thrown = the [Exception] thrownBy Rational(2,3).toInt
//    println(s"thrown: $thrown")
//        thrown.getMessage should equal ("[2/3] is not Whole")
  }

	"2/4" should "not be OK" in {
		val thrown = the [IllegalArgumentException] thrownBy Rational(2,4)
				thrown.getMessage should equal ("requirement failed: Rational(2,4): arguments have common factor: 2")
	}
	it should "be OK via normalize" in {
		Rational.normalize(2,4)
	}
  
  "Floating Point Problem" should "be OK" in {
    val x = Rational(1,10)+Rational.normalize(2,10)
    val y = x * 10 / 3
    y shouldBe 'unity
  }
}