package edu.neu.coe.scala

import org.scalatest.{ FlatSpec, Matchers }
import rng._

/**
 * @author scalaprof
 */
class RNGSpec extends FlatSpec with Matchers {
  
  def sum(xs: Seq[Double]): Double = ???
  def sumU(xs: Seq[UniformDouble]): Double = xs.foldLeft(0.0)((a,x)=>x+a)
  def mean(xs: Seq[Double]) = sum(xs)/xs.length
  def meanU(xs: Seq[UniformDouble]) = sumU(xs)/xs.length

  "RNG(0L)" should "match case RNG(-4962768465676381896L)" in {
    val r: RNG[Long] = LongRNG(0L)
    r.next should matchPattern {case LongRNG(-4962768465676381896L) =>}
  }
  it should "match case RNG(4804307197456638271L) on next" in {
    val r: RNG[Long] = LongRNG(0L)
    r.next.next should matchPattern {case LongRNG(4804307197456638271L) =>}
  }
  "7th element of RNG(0)" should "match case RNG(-4962768465676381896L)" in {
    val lrs = RNG.rngs(LongRNG(0)) take 7 toList;    
    (lrs last) should matchPattern {case LongRNG(488730542833106255L) =>}
  }
  "Double stream" should "have zero mean" in {
    val xs = RNG.values(DoubleRNG.apply(0)) take 1001 toList;
    (math.abs(mean(xs))) shouldBe <= (5E-3)
  }
  "0..1 stream" should "have mean = 0.5 using rngs" in {
    val xs = RNG.values(UniformDoubleRNG.apply(0)) take 1001 toList;
    (math.abs(meanU(xs)-0.5)) shouldBe <= (5E-3)
  }
  it should "have mean = 0.5 using values(rngs)" in {
    val xs = RNG.values(UniformDoubleRNG.apply(0)) take 1001 toList;
    (math.abs(meanU(xs)-0.5)) shouldBe <= (5E-3)
  }
  "Gaussian stream" should "have mean = 0 using values2(rngs)" in {
    val xs = RNG.values2(GaussianRNG.apply(0)) take 11111 toList;
    (math.abs(mean(xs))) shouldBe <= (5E-3)
  }
}