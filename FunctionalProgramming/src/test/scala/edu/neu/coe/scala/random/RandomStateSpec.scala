package edu.neu.coe.scala.random

import org.scalatest.{FlatSpec, Matchers}

import scala.language.postfixOps

/**
  * @author scalaprof
  */
class RandomStateSpec extends FlatSpec with Matchers {

  def stdDev(xs: Seq[Double]): Double = math.sqrt(xs.reduceLeft((a, x) => a + x * x)) / xs.length

  def mean(xs: Seq[Double]) = xs.sum / xs.length

  // XXX Clearly, this doesn't look good. We will soon learn how to write
  // generic methods like sum and mean. But for now, this is what we've got.
  def sumU(xs: Seq[UniformDouble]): Double = xs.foldLeft(0.0)((a, x) => x + a)

  def meanU(xs: Seq[UniformDouble]) = sumU(xs) / xs.length

  "RandomState(0L)" should "match case RandomState(4804307197456638271)" in {
    val r: RandomState[Long] = RandomState(0L)
    r.next should matchPattern { case JavaRandomState(4804307197456638271L,identity) => }
  }
  it should "match case RandomState(-1034601897293430941) on next" in {
    val r: RandomState[Long] = RandomState(0L)
    r.next.next should matchPattern { case JavaRandomState(-1034601897293430941L,identity) => }
  }
  "7th element of RandomState(0)" should "match case RandomState(5082315122564986995L)" in {
    val lrs = RandomState(0).toStream.drop(6).take(1)
    (lrs head) should matchPattern { case 5082315122564986995L => }
  }
  "Double stream" should "have zero mean" in {
    val xs = RandomState(0).map(RandomState.longToDouble).toStream take 10001 toList;
    val mu = mean(xs) //xs.sum/xs.length
    math.abs(mu) shouldBe <= (2E-2)
  }
  "0..1 stream" should "have mean = 0.5" in {
    val xs = RandomState(0).map(RandomState.longToDouble).map(RandomState.doubleToUniformDouble).toStream take 1001 toList;
    math.abs(meanU(xs) - 0.5) shouldBe <=(5E-3)
  }
  "map" should "work" in {
    val rLong: RandomState[Long] = RandomState(0)
    val rInt = rLong.map(_.toInt)
    rInt.get shouldBe -723955400
    val next = rInt.next
    next.get shouldBe 406937919
    val next2 = next.next
    next2.get shouldBe 1407270755
  }
  it should "work with map of map" in {
    val rLong: RandomState[Long] = RandomState(0L)
    val rInt = rLong.map(_.toInt)
    val rBoolean = rInt.map(_ % 2 == 0)
    rBoolean.get shouldBe true
  }
  "flatMap" should "work" in {
    val r1 = RandomState(0)
    val r2 = r1.flatMap(RandomState(_))
    r2.get shouldBe 4804307197456638271L
  }
  "for comprehension" should "work" in {
    val r1 = RandomState(0)
    val z: RandomState[Double] = for (x <- r1; y <- RandomState(x)) yield x.toDouble/Long.MaxValue
    z.get shouldBe -0.5380644352028887 +- 0.0001
  }
}