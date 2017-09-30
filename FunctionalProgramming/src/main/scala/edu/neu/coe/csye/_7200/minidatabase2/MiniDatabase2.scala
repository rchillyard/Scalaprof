package edu.neu.coe.csye._7200.minidatabase2

import scala.io.Source
import scala.util._

/**
 * @author scalaprof
 */
object MiniDatabase2 extends App {
  
  // TODO 1: Implement this method, similar to the map2 you already know (4 points)
  def map3[A,B,C,D](a: Option[A], b: Option[B], c: Option[C])(f: (A,B,C) => D): Option[D] = ???

  // TODO 2: Implement this method, similar to the map2 you already know (4 points)
  def map2[A,B,C](a: Try[A], b: Try[B])(f: (A,B) => C): Try[C] = ???

  def load(filename: String) = {
    val src = Source.fromFile(filename)
    val database = src.getLines.toList.map(e => Entry.parse(e.split(",")))
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
  
  if (args.length>0) {
    val db = load(args(0))
    print(db)
  }
}

case class Entry(name: Name, height: Height)

case class Height(feet: Int, in: Int) {
  def inches = feet*12+in
}

object Entry {
  // TODO 3: Implement this method using the map2 method you implemented above, using a suitable function f (5 points)
  def parse(name: Try[Name],height: Try[Height]): Try[Entry] = ???
  def parse(name: String, height: String): Try[Entry] = parse(Name.parse(name),Height.parse(height))
  // TODO 4: Implement this method using the entry(0) and entry(3) for the name and height and using parse (immediately above) (8 points)
  def parse(entry: Seq[String]): Try[Entry] = ???
}

object Height {  
  // TODO 5: Implement this method using the map2 method you implemented above (7 points)
  def parse(ft: String, in: String): Try[Height] = ???
  val rHeightFtIn = """^\s*(\d+)\s*(?:ft|\')(\s*(\d+)\s*(?:in|\"))?\s*$""".r
  // TODO 6: Implement this method using the rHeightFtIn pattern given above and the parse method you just implemented (5 points)
  def parse(height: String): Try[Height] = ???
}

case class Name(first: String, middle: Option[String], last: String)

object Name {
  val rName="""^(\w+)\s+((.*)\s+)?(\w+)$""".r
  // TODO 7: Implement this method using the rName pattern given above (7 points)
  def parse(name: String): Try[Name] = ???
}