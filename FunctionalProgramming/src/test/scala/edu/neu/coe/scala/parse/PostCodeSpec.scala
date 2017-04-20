package edu.neu.coe.scala.parse

import org.scalatest.{FlatSpec, Matchers}

/**
  * @author scalaprof
  */
class PostCodeSpec extends FlatSpec with Matchers {
  behavior of "letter"
  it should "parse A" in {
    val parser = new PostCode
    val r = parser.parseAll(parser.letter, "A")
    r should matchPattern { case parser.Success("A", _) => }
  }
  behavior of "unit"
  it should "parse JX" in {
    val parser = new PostCode
    val r = parser.parseAll(parser.unit, "JX")
    r should matchPattern { case parser.Success("JX", _) => }
  }
  behavior of "area"
  it should "parse A" in {
    val parser = new PostCode
    val r = parser.parseAll(parser.area, "A")
    r should matchPattern { case parser.Success("A", _) => }
  }
  it should "parse AB" in {
    val parser = new PostCode
    val r = parser.parseAll(parser.area, "AB")
    r should matchPattern { case parser.Success("AB", _) => }
  }
  behavior of "digit"
  it should "parse 1" in {
    val parser = new PostCode
    val r = parser.parseAll(parser.digit, "1")
    r should matchPattern { case parser.Success(parser.Digit(1), _) => }
  }
  behavior of "district"
  it should "parse 1A" in {
    val parser = new PostCode
    val r = parser.parseAll(parser.district, "1A")
    r should matchPattern { case parser.Success(parser.District(parser.Digit(1),None,Some('A')), _) => }
  }
  behavior of "inwardCode"
  it should "parse 7JX" in {
    val parser = new PostCode
    val r = parser.parseAll(parser.inwardCode, "7JX")
    r should matchPattern { case parser.Success(parser.InwardCode(parser.Digit(7),"JX"), _) => }
  }
  it should "parse 1BB" in {
    val parser = new PostCode
    val r = parser.parseAll(parser.inwardCode, "1BB")
    r should matchPattern { case parser.Success(parser.InwardCode(parser.Digit(1),"BB"), _) => }
  }
  behavior of "outwardCode"
  it should "parse EC1A" in {
    val parser = new PostCode
    val r = parser.parseAll(parser.outwardCode, "EC1A")
    r should matchPattern { case parser.Success(parser.OutwardCode("EC",parser.District(parser.Digit(1),None,Some('A'))), _) => }
  }
  behavior of "postCode"
  val code1 = "EC1A 1BB"
  val code2 = "W1A 0AX"
  val code3 = "M1 1AE"
  val code4 = "B33 8TH"
  val code5 = "CR2 6XH"
  val code6 = "DN55 1PT"
  it should "parse "+code1 in {
    val parser = new PostCode
    val expected = parser.PostCode(parser.OutwardCode("EC", parser.District(parser.Digit(1), None, Some('A'))), parser.InwardCode(parser.Digit(1), "BB"))
    val r = parser.parseAll(parser.postCode, code1)
    r should matchPattern { case parser.Success(`expected`, _) => }
    r.get.toString shouldBe code1
  }
  it should "parse "+code2 in {
    val parser = new PostCode
    val r = parser.parseAll(parser.postCode, code2)
    val expected = parser.PostCode(parser.OutwardCode("W", parser.District(parser.Digit(1), None, Some('A'))), parser.InwardCode(parser.Digit(0), "AX"))
    r should matchPattern { case parser.Success(`expected`, _) => }
    r.get.toString shouldBe code2
  }
  it should "parse "+code3 in {
    val parser = new PostCode
    val r = parser.parseAll(parser.postCode, code3)
    val expected = parser.PostCode(parser.OutwardCode("M", parser.District(parser.Digit(1), None, None)), parser.InwardCode(parser.Digit(1), "AE"))
    r should matchPattern { case parser.Success(`expected`, _) => }
    r.get.toString shouldBe code3
  }
  it should "parse "+code4 in {
    val parser = new PostCode
    val r = parser.parseAll(parser.postCode, code4)
    val expected = parser.PostCode(parser.OutwardCode("B", parser.District(parser.Digit(3), Some(parser.Digit(3)), None)), parser.InwardCode(parser.Digit(8), "TH"))
    r should matchPattern { case parser.Success(`expected`, _) => }
    r.get.toString shouldBe code4
  }
  it should "parse "+code5 in {
    val parser = new PostCode
    val r = parser.parseAll(parser.postCode, code5)
    val expected = parser.PostCode(parser.OutwardCode("CR", parser.District(parser.Digit(2), None, None)), parser.InwardCode(parser.Digit(6), "XH"))
    r should matchPattern { case parser.Success(`expected`, _) => }
    r.get.toString shouldBe code5
  }
  it should "parse "+code6 in {
    val parser = new PostCode
    val r = parser.parseAll(parser.postCode, code6)
    val expected = parser.PostCode(parser.OutwardCode("DN", parser.District(parser.Digit(5), Some(parser.Digit(5)), None)), parser.InwardCode(parser.Digit(1), "PT"))
    r should matchPattern { case parser.Success(`expected`, _) => }
    r.get.toString shouldBe code6
  }
}
