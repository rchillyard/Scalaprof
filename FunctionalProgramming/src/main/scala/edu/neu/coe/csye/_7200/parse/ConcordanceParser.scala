package edu.neu.coe.csye._7200.parse

import scala.io.Source
import scala.util.parsing.combinator._

/**
 * @author scalaprof
 */
class ConcordanceParser extends RegexParsers {
  val rWord = """[\w’]+[,;\.\-\?\!\—]?""".r
  def word: Parser[(Int,String)] = new PositionalParser(regex(rWord) ^^ { case w => w })
  def sentence: Parser[Seq[(Int,String)]] = rep(word) ^^ { case s: Seq[(Int,String)] => s }
    
  class PositionalParser(p: Parser[String]) extends Parser[(Int,String)] {
    def apply(in: Input): ParseResult[(Int,String)] =
      p.apply(in) match {
        case Success(w,pos) => Success((pos.offset-w.length+1,w),pos)
        case f @ Failure(_,_) => f
        case _ => Failure("PositionalParser: logic error",in)
      }
    }  
}

object ConcordanceParser {
 
  def main(args: Array[String]): Unit = {
    val docs = for (f <- args) yield Source.fromFile(f).mkString
    val concordance = for (i <- 0 to docs.length-1) yield (args(i),parseDoc(docs(i)))
    println(concordance)
    // an alternative way of looking at the data (gives doc, page, line and char numbers with each string)
    val q = for {(d,xxxx) <- concordance; (p,xxx) <- xxxx; (l,xx) <- xxx; (c,x) <- xx} yield (d, p,l,c,x)
    println(q)
    // yet another way to look at the data
    val concordanceMap = concordance.toMap
    println(concordanceMap)
  }
  
  def parseDoc(content: String) = {
    val pages = for (p <- content.split("/p")) yield p
    for (i <- 0 to pages.length-1) yield (i+1,parsePage(pages(i)))
  }

  def parsePage(content: String) = {
    val lines = for (l <- content.split("\n")) yield l
    for (i <- 0 to lines.length-1) yield (i+1,parseLine(lines(i)))
  }

  def parseLine(line: String): Seq[(Int,String)] = {
    def tidy(s: String) = s.replaceAll("""[.,;-?!]""", "")
    val p = new ConcordanceParser
    val r = p.parseAll(p.sentence,line) match {
      case p.Success(ws,_) => ws
      case p.Failure(e,_) => println(e); List()
      case _ => println("PositionalParser: logic error"); List()
    }
    r map {case (i,s) => (i,tidy(s).toLowerCase)}
  }
}