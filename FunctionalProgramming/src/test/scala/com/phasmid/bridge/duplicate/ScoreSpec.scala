package com.phasmid.bridge.duplicate

import org.scalatest.{FlatSpec, Matchers}

/**
  * @author scalaprof
  */
class ScoreSpec extends FlatSpec with Matchers {
  "event" should "parse" in {
    val parser = new RecapParser
    val r = parser.parseAll(parser.event, "Test Section 2016/04/12. A 1 N Erithacus Rubecula. Esox Lucius.\nT 1\n1 1 130\n2 2 150\n.T 2\n1 1 130\n2 2 150\n.")
    r should matchPattern { case parser.Success(_, _) => }
    r.get.title shouldBe "Test Section 2016/04/12"
    r.get.sections.size shouldBe 1
  }
  "section" should "parse" in {
    val parser = new RecapParser
    val r = parser.parseAll(parser.section, "A 1 N Erithacus Rubecula. Esox Lucius.\nT 1\n1 1 130\n2 2 150\n.T 2\n1 1 130\n2 2 150\n.")
    r should matchPattern { case parser.Success(_, _) => }
    r.get.preamble.identifier shouldBe "A"
    r.get.preamble.pairs.size shouldBe 1
    r.get.travelers.size shouldBe 2
  }
  "preamble" should "parse" in {
    val parser = new RecapParser
    val r = parser.parseAll(parser.preamble, "A 1 N Erithacus Rubecula. Esox Lucius.")
    r should matchPattern { case parser.Success(_, _) => }
    r.get.identifier shouldBe "A"
    r.get.pairs.size shouldBe 1
  }
  "players" should "parse" in {
    val parser = new RecapParser
    val r = parser.parseAll(parser.pair, "1 N Erithacus Rubecula. Esox Lucius.")
    r should matchPattern { case parser.Success(_, _) => }
    r.get.number shouldBe 1
    r.get.direction shouldBe "N"
    r.get.names should matchPattern {case (_, _) => }
    r.get.names._1 shouldBe "Erithacus Rubecula"

  }
  "traveler" should "parse" in {
    val parser = new RecapParser
    val r = parser.parseAll(parser.traveler, "T 1\n1 1 130\n2 2 150\n.")
    r should matchPattern { case parser.Success(_, _) => }
    r.get.board shouldBe 1
    r.get.ps.size shouldBe 2
  }
  it should "matchpoint properly (1)" in {
    val p1 = Play(2,1,PlayResult(Right(130)))
    val p2 = Play(1,2,PlayResult(Right(150)))
    val t = Traveler(1,Seq(p1,p2))
    t.matchpoint(p1) shouldBe Some(Rational.zero)
    t.matchpoint(p2) shouldBe Some(Rational.one)
  }
  it should "matchpoint properly (2)" in {
    val p1 = Play(2,1,PlayResult(Right(130)))
    val p2 = Play(1,2,PlayResult(Right(150)))
    val t = Traveler(1,Seq(p1,p2))
    val mps = t.matchpointIt
    mps.head.mp shouldBe Some(Rational.zero)
    mps.tail.head.mp shouldBe Some(Rational.one)
  }
  it should "matchpoint properly (3)" in {
    val p1 = Play(2,1,PlayResult(Right(130)))
    val p2 = Play(1,2,PlayResult(Right(150)))
    val p3 = Play(3,3,PlayResult(Right(150)))
    val t = Traveler(1,Seq(p1,p2,p3))
    val mps = t.matchpointIt
    mps.head.mp shouldBe Some(Rational.zero)
    mps.tail.head.mp shouldBe Some(Rational(3,4))
    Score.mpsAsString(mps.tail.head.mp.get,2) shouldBe "1.5"
    mps.tail.tail.head.mp shouldBe Some(Rational(3,4))
  }
  it should "calculate BAM mps" in {
    val traveler = "   T 1\n    1 1 420\n    2 2 430."
    val parser = new RecapParser
    val r = parser.parseAll(parser.traveler, traveler)
    r should matchPattern { case parser.Success(_, _) => }
    val t = r.get
    val firstEntry = t.ps.head
    val mps = firstEntry.matchpoints(t)
    mps shouldBe Some(Rational.zero)
  }
  it should "calculate mps" in {
    val traveler = "   T 1\n    1 1 420\n    2 2 420\n    3 4 420\n    4 3 140\n    5 5 170\n    6 6 -50\n    7 6 420."
    val parser = new RecapParser
    val r = parser.parseAll(parser.traveler, traveler)
    r should matchPattern { case parser.Success(_, _) => }
    val t = r.get
    val firstEntry = t.ps.head
    val mps = firstEntry.matchpoints(t)
    mps shouldBe Some(Rational(3,4))
  }
  "play" should "parse 1 1 130" in {
    val parser = new RecapParser
    val r = parser.parseAll(parser.play, "1 1 130")
    r should matchPattern { case parser.Success(_, _) => }
    r.get.ns shouldBe 1
    r.get.ew shouldBe 1
    r.get.result should matchPattern { case PlayResult(Right(130)) => }
  }
  it should "compare 1 1 +130 with 2 2 110 as 2" in {
    val p1 = Play(1,1,PlayResult(Right(130)))
    val p2 = Play(2,2,PlayResult(Right(110)))
    p1.compare(p2.result) shouldBe Some(0)
    p2.compare(p1.result) shouldBe Some(2)
  }
  "result" should "parse 130" in {
    val parser = new RecapParser
    val r = parser.parseAll(parser.result, "130")
    r should matchPattern { case parser.Success(_, _) => }
     r.get should matchPattern { case PlayResult(Right(130)) => }
  }
  it should "score DNP as None" in {
    val p1 = Play(1,1,PlayResult(Left("DNP")))
    val t = Traveler(1, Seq())
    p1.matchpoints(t) shouldBe None
  }
  it should "score A as Some(1/2)" in {
    val p1 = Play(1,1,PlayResult(Left("A")))
    val t = Traveler(1, Seq())
    p1.matchpoints(t) shouldBe Some(Rational(1,2))
  }
  it should "score A- as Some(2,5)" in {
    val p1 = Play(1,1,PlayResult(Left("A-")))
    val t = Traveler(1, Seq())
    p1.matchpoints(t) shouldBe Some(Rational(2,5))
  }
  it should "parse DNP" in {
    val parser = new RecapParser
    val r = parser.parseAll(parser.result, "DNP")
    r should matchPattern { case parser.Success(_, _) => }
    r.get should matchPattern { case PlayResult(Left("DNP")) => }
  }
  it should "parse A-" in {
    val parser = new RecapParser
    val r = parser.parseAll(parser.result, "A-")
    r should matchPattern { case parser.Success(_, _) => }
    r.get should matchPattern { case PlayResult(Left("A-")) => }
  }
  "mpsAsPercentage" should "work" in {
    val r = Rational(3,4)
    Score.mpsAsPercentage(r,1) shouldBe "75.00%"
  }
  "mpsAsString" should "work" in {
    val r = Rational(3,4)
    Score.mpsAsString(r,6) shouldBe "4.5"
  }
 }
