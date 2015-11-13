package edu.neu.coe.scala

import org.scalatest.{ FlatSpec, Matchers }
import rng._

/**
 * @author scalaprof
 */
class RNGSpec extends FlatSpec with Matchers {

  "RNG(0L)" should "match case RNG(-4962768465676381896L)" in {
    val r: RNG[Long] = LongRNG(0L)
    r.next should matchPattern {case LongRNG(-4962768465676381896L) =>}
  }
  it should "match case RNG(4804307197456638271L) on next" in {
    val r: RNG[Long] = LongRNG(0L)
    r.next.next should matchPattern {case LongRNG(4804307197456638271L) =>}
  }
  "7th element of RNG(0)" should "match case RNG(-4962768465676381896L)" in {
    val l1 = RNG.rngs(LongRNG(0)) take 7 toList;    
    (l1 last) should matchPattern {case LongRNG(488730542833106255L) =>}
  }
  "Double stream" should "have zero mean" in {
    val l1 = RNG.rngs(DoubleRNG.apply(0)) take 1001 toList;
    val mean = (l1.foldLeft(0.0)(_ + _.value))/l1.length
    (math.abs(mean)) shouldBe <= (5E-3)
  }
  "0..1 stream" should "have mean = 0.5 using rngs" in {
    val l1 = RNG.rngs(UniformDoubleRNG.apply(0)) take 1001 toList;
    def sum(r: Double, x: RNG[UniformDouble]): Double = (l1.foldLeft(0.0)((r,x) => x.value+r)) // ???
    val mean = l1.foldLeft(0.0)(sum(_,_)) /l1.length
    (math.abs(mean-0.5)) shouldBe <= (5E-3)
  }
  it should "have mean = 0.5 using values(rngs)" in {
    val l1 = RNG.values(RNG.rngs(UniformDoubleRNG.apply(0))) take 1001 toList;
    val mean: Double = (l1.foldLeft(0.0)((r,x) => x+r))/l1.length // ???
    (math.abs(mean-0.5)) shouldBe <= (5E-3)
  }
  "Gaussian stream" should "have mean = 0 using values2(rngs)" in {
    val l1 = RNG.values2(RNG.rngs(GaussianRNG.apply(0))) take 11111 toList;
    val mean = (l1.foldLeft(0.0)(_ + _))/l1.length
    (math.abs(mean)) shouldBe <= (5E-3)
  }
}