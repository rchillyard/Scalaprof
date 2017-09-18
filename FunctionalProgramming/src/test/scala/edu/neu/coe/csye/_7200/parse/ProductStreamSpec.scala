package edu.neu.coe.csye._7200.parse

import java.io.{File, FileInputStream}

import edu.neu.coe.scala.parse.CsvParser
import org.joda.time._
import org.scalatest.{FlatSpec, Matchers}

import scala.util._

/**
 * @author scalaprof
 */
class ProductStreamSpec extends FlatSpec with Matchers {
  """"Hello", "World!"""" should "be (String) stream via CSV" in {
    val c = CSV[Tuple1[String]](Stream("x",""""Hello"""", """"World!""""))
    c.header shouldBe List("x")
    val wts = c.tuples
    wts.head match {
      case Tuple1(s) => assert(s=="Hello")
    }
    wts.tail.head match {
      case Tuple1(s) => assert(s=="World!")
    }
  }
  it should "be (String) stream via TupleStream" in {
    val wts = TupleStream[Tuple1[String]](Stream("x",""""Hello"""", """"World!"""")).tuples
    wts.head match {
      case Tuple1(s) => assert(s=="Hello")
    }
    wts.tail.head match {
      case Tuple1(s) => assert(s=="World!")
    }
  }
  it should "convert to list properly" in {
    val c = CSV[Tuple1[String]](Stream("x",""""Hello"""", """"World!""""))
    val wts = c.asList
    wts.size should be (2)
  }
  it should "convert to map properly" in {
    val c = CSV[Tuple1[String]](Stream("x",""""Hello"""", """"World!""""))
    val wtIm = c toMap {case Tuple1(s) => s.hashCode}
    wtIm.get("Hello".hashCode) should matchPattern { case Some(Tuple1("Hello")) => }
  }
  it should "have column x of type String" in {
    val c = CSV[Tuple1[String]](Stream("x",""""Hello"""", """"World!""""))
    c column[String]("x") match {
      case Some(xs) =>
        xs.take(2).toList.size should be (2)
        xs(0) shouldBe "Hello"
        xs(1) shouldBe "World!"
      case _ => fail("no column projected")
    }
  }
  """"3,5", "8,13"""" should "be (Int,Int) stream" in {
    val iIts = CSV[(Int,Int)](Stream("x,y","3,5", "8,13")).tuples
    iIts.head match {
      case (x,y) => assert(x==3 && y==5)
    }
    iIts.tail.head match {
      case (x,y) => assert(x==8 && y==13)
    }
  }
  it should "be (String,String) stream via TupleStream" in {
    val wWts = TupleStream[(String,String)](Stream("x,y","3,5", "8,13")).tuples
    wWts.head match {
      case (a,b) => assert(a=="3" && b=="5")
    }
    wWts.tail.head match {
      case (a,b) => assert(a=="8" && b=="13")
    }
  }
  it should "map into (Int,Int) via TupleStream" in {
    val wWts = TupleStream[(String,String)](Stream("x,y","3,5", "8,13"))
    val iIts = wWts map {case (x,y) => (x.toInt,y.toInt)}
    iIts.tuples.head match {
      case (a,b) => assert(a==3 && b==5)
      case _ => fail("no match")
    }
  }
  it should "have column y of type Int" in {
    CSV[(Int,Int)](Stream("x,y","3,5", "8,13")) column[Int]("y") match {
      case Some(ys) =>
        ys.take(2).toList.size should be (2)
        ys(0) shouldBe 5
        ys(1) shouldBe 13
      case _ => fail("no column projected")
    }
  }
  it should "convert to map properly" in {
    val c = CSV[(Int,Int)](Stream("x,y","3,5", "8,13"))
    val iItIm = c toMap {case (x,y) => x}
    iItIm.get(8) should matchPattern { case Some((8,13)) => }
  }
  it should "map into (Double,Double) properly" in {
    val c = CSV[(Int,Int)](Stream("x,y","3,5", "8,13"))
    val doubles = c map {case (x,y) => (x.toDouble,y.toDouble)}
    val dDts = doubles.tuples
    dDts.head match {
      case (x,y) => assert(x==3.0 && y==5.0)
    }
  }
  it should "convert into maps properly" in {
    val zWms = CSV[(Int,Int)](Stream("x,y","3,5", "8,13")).asMaps
    zWms(0) should be (Map("x"->3,"y"->5))
    zWms(1) should be (Map("x"->8,"y"->13))
  }
  """"3,5.0", "8,13.5"""" should "be (Int,Double) stream" in {
    val dIts = CSV[(Int,Double)](Stream("x,y","3,5.0", "8,13.5")).tuples
    dIts.head match {
      case (x,y) => assert(x==3 && y==5.0)
    }
    dIts.tail.head match {
      case (x,y) => assert(x==8 && y==13.5)
    }
  }
  """"milestone 1, 2016-03-08", "milestone 2, 2016-03-15"""" should "be (String,Datetime) stream" in {
    val dIts = CSV[(String,DateTime)](Stream("event,date","milestone 1,2016-03-08", "milestone 2,2016-03-15")).tuples
    dIts.head match {
      case (x,y) => assert(x=="milestone 1" && y==new DateTime("2016-03-08"))
    }
    dIts.tail.head match {
      case (x,y) => assert(x=="milestone 2" && y==new DateTime("2016-03-15"))
    }
  }
  "sample.csv" should "be (String,Int) stream" in {
    val iWts = CSV[(String,Int)](new FileInputStream(new File("sample.csv"))).tuples
    iWts.head match {
      case (x,y) => assert(x=="Sunday" && y==1)
    }
    iWts.tail.head match {
      case (x,y) => assert(x=="Monday" && y==2)
    }
    iWts.size should be (8)
    ((iWts take 8).toList)(7) should be ("TGIF, Bruh", 8)
  }
  it should "be (String,Int) stream using File" in {
    val iWts = CSV[(String,Int)](new File("sample.csv")).tuples
    iWts.head match {
      case (x,y) => assert(x=="Sunday" && y==1)
    }
    iWts.tail.head match {
      case (x,y) => assert(x=="Monday" && y==2)
    }
    iWts.size should be (8)
    ((iWts take 8).toList)(7) should be ("TGIF, Bruh", 8)
  }
  it should "be (String,Int) stream using URI" in {
    val iWts = CSV[(String,Int)](getClass.getResource("sample.csv").toURI).tuples
    iWts.head match {
      case (x,y) => assert(x=="Sunday" && y==1)
    }
    iWts.tail.head match {
      case (x,y) => assert(x=="Monday" && y==2)
    }
    iWts.size should be (8)
    ((iWts take 8).toList)(7) should be ("TGIF, Bruh", 8)
  }
}

class CsvParserSpec extends FlatSpec with Matchers {
  val defaultParser = CsvParser()
  "CsvParser()" should """parse "x" as Success(List("x"))""" in {
    defaultParser.parseRow(""""x"""") should matchPattern { case scala.util.Success(List("x")) => }
  }
  it should """parse "x,y" as Success(List("x,y"))""" in {
    defaultParser.parseRow(""""x,y"""") should matchPattern { case scala.util.Success(List("x,y")) => }
  }
  it should """parse "x,y" as Success(List("x","y")""" in {
    defaultParser.parseRow("x,y") should matchPattern { case scala.util.Success(List("x","y")) => }
  }
  val pipeParser = CsvParser("|")
  """"CsvParser("|")"""" should """parse "|" as Success(List("|"))""" in {
    pipeParser.parseRow(""""|"""") should matchPattern { case scala.util.Success(List("|")) => }
  }
  it should """parse x,y as Success(List("x,y"))""" in {
    pipeParser.parseRow("x,y") should matchPattern { case scala.util.Success(List("x,y")) => }
  }
  it should """parse x,y as Success(List("x","y")""" in {
    pipeParser.parseRow("x|y") should matchPattern { case scala.util.Success(List("x","y")) => }
  }
  val customParser = CsvParser("|","'")
  """"CsvParser("|","'")"""" should """parse '|' as Success(List("|"))""" in {
    customParser.parseRow("'|'") should matchPattern { case scala.util.Success(List("|")) => }
  }
  it should """parse x,y as Success(List("x,y"))""" in {
    customParser.parseRow("x,y") should matchPattern { case scala.util.Success(List("x,y")) => }
  }
  it should """parse x,y as Success(List("x","y")""" in {
    customParser.parseRow("x|y") should matchPattern { case scala.util.Success(List("x","y")) => }
  }
  "CsvParser.parseElem" should "parse 1 as 1" in (CsvParser.parseElem("1") should matchPattern { case Success(1) => })
  it should "parse 1.0 as 1.0" in (CsvParser.parseElem("1.0") should matchPattern { case Success(1.0) => })
  it should "parse true as true" in (CsvParser.parseElem("true") should matchPattern { case Success(true) => })
  it should "parse false as false" in (CsvParser.parseElem("false") should matchPattern { case Success(false) => })
  it should "parse yes as yes" in (CsvParser.parseElem("yes") should matchPattern { case Success(true) => })
  it should "parse no as false" in (CsvParser.parseElem("no") should matchPattern { case Success(false) => })
  it should "parse T as true" in (CsvParser.parseElem("T") should matchPattern { case Success(true) => })
  it should """parse "1" as "1"""" in (CsvParser.parseElem(""""1"""") should matchPattern { case Success("1") => })
  it should """parse 2016-03-08 as datetime""" in {
    val dt = CsvParser.parseElem("2016-03-08")
    dt should matchPattern { case Success(d) => }
//    dt.get shouldBe new DateTime("2016-03-08")
  }
  def putInQuotes(w: String): Try[Any] = Success(s"""'$w'""")
  val customElemParser = CsvParser(elemParser=(putInQuotes _))
  "custom element parser" should "parse 1 as '1'" in (customElemParser.elementParser("1") should matchPattern { case Success("'1'") => })
  it should "parse 1.0 as '1.0'" in (customElemParser.elementParser("1.0") should matchPattern { case Success("'1.0'") => })
  it should "parse true as 'true'" in (customElemParser.elementParser("true") should matchPattern { case Success("'true'") => })
  it should """parse "1" as '"1"'""" in (customElemParser.elementParser(""""1"""") should matchPattern { case Success("""'"1"'""") => })

  "CsvParser.parseDate" should "work" in {
    val dt = CsvParser.parseDate(CsvParser.dateFormatStrings)("2016-03-08")
    dt should matchPattern { case Success(x) => }
    dt.get shouldBe new DateTime("2016-03-08T00:00:00.0")    
  }
}
