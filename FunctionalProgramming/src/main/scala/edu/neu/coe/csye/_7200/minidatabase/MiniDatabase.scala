package edu.neu.coe.csye._7200.minidatabase

import scala.io.Source
import scala.util._
import scala.util.matching._

/**
 * @author scalaprof
 */
object MiniDatabase extends App {
  def load(filename: String) = {
    val src = Source.fromFile(filename)
    val database = src.getLines.toList.map(e => Entry(e.split(",")))
    val result = database
    src.close
    result
  }
  
  def measure(height: Height) = height match {
    case Height(8,_) => "giant"
    case Height(7,_) => "very tall"
    case Height(6,_) => "tall"
    case Height(5,_) => "normal"
    case Height(_,_) => "short"
  }
  
//  def main(args: Array[String]): Unit = {
    if (args.length>0) {
      val db = load(args(0))
      print(db)
    }
//  }
}

case class Entry(name: Name, social: Social, dob: Date, height: Height, weight: Int)

case class Height(feet: Int, in: Int) {
  def inches = feet*12+in
}

object Entry {
  def apply(name: String, social: String, dob: String, height: String, weight: String): Entry =
    Entry(Name(name),Social(social),Date(dob),Height(height),weight.toInt)
  def apply(entry: Seq[String]): Entry = apply(entry.head,entry(1),entry(2),entry(3),entry(4))
}

object Height {
  val rHeightFtIn = """^\s*(\d+)\s*(?:ft|\')(\s*(\d+)\s*(?:in|\"))?\s*$""".r
  val rHeightFt = """^\s*(\d+)\s*(?:ft|\')$""".r
  def apply(ft: String, in: String) = new Height(ft.toInt,in.toInt)
  def apply(ft: Int) = new Height(ft,0)
  def apply(height: String): Height = height match {
    case rHeightFt(ft) => Height(ft.toInt)
    case rHeightFtIn(ft,_,in) => Height(ft,in)
    case _ => throw new IllegalArgumentException(height)
  }
}

case class Name(first: String, middle: String, last: String)

case class Social(area: Int, group: Int, serial: Int)

case class Date(year: Int, month: Int, day: Int)

object Name {
  val rName3 = """^(\w+)\s+(\w.*)\s+(\w+)$""".r
  val rName2 = """^(\w+)\s+(\w+)$""".r
  val rName1 = """^(\w+)$""".r
  def apply(name: String): Name = name match {
    case rName3(first,middle,last) => Name(first,middle,last)
    case rName2(first,last) => Name(first,"",last)
    case rName1(s) => Name("","",s)
    case _ => println(s"""cannot match: "$name""""); Name("","","")
  }
}

object Date {
  val monthConversion = Map("Jan"->1, "Feb"->2, "Mar"->3, "Apr"->4, "May"->5, "Jun"->6,
    "Jul"->7,"Aug" -> 8, "Sep"->9,"Oct"->10,"Nov"->11,"Dec"->12 )
  val rDate1 = """^(\w+)\s+(\d+)\w\w\s(\d{4})$""".r
  val rDate2 = """^(\d+)\/(\d+)\/(\d+)$""".r
  def apply(year: String, month: String, day: String): Date = Date(year.toInt,month.toInt,day.toInt) 
  def apply(date: String): Date = date match {
    case rDate1(m,d,y) => Date(monthConversion.get(m) match {
      case Some(x) => x
      case _ => println(s"""cannot match month: "m""""); 0
    },d.toInt,y.toInt)
    case rDate2(m,d,y) => Date(m,d,y)
    case _ => println(s"""cannot match date: "$date""""); Date(0,0,0)
  }
}

object Social {
  val rSsn = """^(\d{3})\-(\d{2})\-(\d{4})$""".r
  def apply(ssn: String): Social = ssn match {
    case rSsn(x,y,z) => Social(x.toInt,y.toInt,z.toInt)
    case _ => println(s"""cannot match SSN: "$ssn""""); Social(0,0,0)
  }
}
