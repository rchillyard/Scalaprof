package edu.neu.coe.scala.minidatabase

import scala.io.Source
import java.io.File

object ListWorksheet {
  println("Welcome to the MiniDatabase worksheet")
	val minidatabase = getClass.getResource("/edu/neu/coe/scala/minidatabase.csv")

    val src = Source.fromURL(minidatabase)
    
//    val words = for (
//    	l <- src.getLines;
//    	w: String <- l.split(",").toSeq;
//    	e <- Entry(w);
//    	) yield w
//    words.toList
    
    val lines = src.getLines
    val words = lines map {e => e.split(",")} toList;
    val entry = words map {w => Entry(w)}
    entry toList
    
    
    object Height {
    import scala.util.Try
  val rHeightFtIn = """^\s*(\d+)\s*(?:ft|\')(\s*(\d+)\s*(?:in|\"))?\s*$""".r
  def parse(height: (String,String)) = (Try{height._1.toInt},Try{height._2.toInt})
  def apply(
  def apply(height: String): Height = height match {
    case rHeightFtIn(ft,_,in) => parse((ft,in))
    case _ => throw new IllegalArgumentException(height)
  }
}
    src.close

}