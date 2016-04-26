package com.phasmid.bridge.duplicate


import scala.io.Source
import scala.language.postfixOps
import scala.util._
import scala.util.parsing.combinator._
import edu.neu.coe.scala.MonadOps

/**
  * Created by scalaprof on 4/12/16.
  */
object Score extends App {
  val us = true
  def mpsAsString(r: Rational, top: Int) = (r * top).toDouble.toString
  def mpsAsPercentage(r: Rational, boards: Int) = "%2.2f".format((r * 100 / boards).toDouble)+"%"

  def readEvent(s: Source): Try[Event] = {
    val p = new RecapParser
    p.parseAll(p.event, s.mkString) match {
      case p.Success(e, _) => Success(e)
      case p.Failure(f, x) => Failure(new Exception(s"parse failure: $f at $x"))
      case p.Error(f, x) => Failure(new Exception(s"parse error: $f at $x"))
    }
  }

  if (args.length > 0) {
    val et = readEvent(Source.fromFile("/Users/scalaprof/RealRobins-nest/bridge/travelers"))
    for (e <- et) {
      println(e.title)
      val results: Map[Preamble, Seq[Result]] = e.createResults
      for ((k,rs) <- results) {
        println(s"Section ${k.identifier}")
        for (r <- rs) {
          val top = r.top
          val direction = if (r.isNS) "N/S" else "E/W"
          println(s"Results for direction: $direction")
          for (s <- r.card.toSeq.sortBy(_._2._1).reverse)
            println(s"${s._1} : ${Score.mpsAsString(s._2._1, top)} : ${Score.mpsAsPercentage(s._2._1,s._2._2)} : ${k.getNames(r.isNS, s._1)}")
        }
      }
      println("=====================================================")
      println("=====================================================")
      println(e)
    }
  }
  else
    System.err.println("Syntax: Score filename")
}

case class Event(title: String, sections: Seq[Section]) {
  if (sections.isEmpty)
    System.err.println("Warning: there are no sections in this event")
  override def toString = {
    val result = StringBuilder.newBuilder
    result.append(s"$title\n")
    for (s <- sections) result.append(s"$s\n")
    result.toString
  }
  def createResults = (for (s <- sections) yield s.preamble->s.createResults).toMap
}

case class Section(preamble: Preamble, travelers: Seq[Traveler]) {
  if (travelers.isEmpty)
    System.err.println("Warning: there are no travelers in this section")
  override def toString = {
    val result = StringBuilder.newBuilder
    result.append(s"$preamble\n")
    for (t <- travelers) result.append(s"$t\n")
    result.toString
  }
  def createResults = {
    val top = calculateTop
    val recap: Seq[Matchpoints] = for (t <- travelers; m <- t.matchpointIt) yield m
    def all(n: Int, dir: Boolean): Seq[Rational] = recap.filter{ m => m.matchesPair(n, dir)} flatMap { m => m.getMatchpoints(dir)}
    def total(d: Boolean): Seq[(Int, (Rational,Int))] = for (p <- preamble.pairs; x = all(p.number, d)) yield p.number -> (x.sum,x.size)
    for (d <- Seq(true,false)) yield Result(d, top, total(d).toMap)
  }
  def calculateTop: Int = {
    val tops: Seq[Int] = for (t <- travelers) yield t.top
    val theTop = tops.distinct
    if (theTop.size != 1) println(s"Warning: not all boards have been played the same number of times: $tops")
    theTop.head
  }
}

/**
  * This represents the "preamble" to a section of an event.
  *
  * @param identifier the section identifier (a single or double upper-case letter)
  * @param pairs a list of the pairs in this section
  */
case class Preamble(identifier: String, pairs: Seq[Players]) {
  if (pairs.isEmpty)
    System.err.println("Warning: there are no players in this section")
  def getNames(ns: Boolean, n: Int) = pairs.filter{ p => p.number==n} map { p => if (ns) p.names._1 else p.names._2} head
  override def toString = {
    val result = StringBuilder.newBuilder
    result.append(s"$identifier\n")
    for (p <- pairs) result.append(s"$p\n")
    result.toString
  }
}

case class Players(number: Int, direction: String, names: (String,String)) {
  override def toString = s"$number$direction: ${names._1} & ${names._2}"
}

object Players {
  def apply(n: String, d: String, a: String, b: String): Players = apply(n.toInt,d,(a,b))
}

/**
  * This is the complete results for a particular direction
  *
  * @param isNS true if this result is for N/S; false if for E/W
  * @param top top on a board
  * @param card a map of tuples containg total score and number of boards played, indexed by the pair number
  */
case class Result(isNS: Boolean, top: Int, card: Map[Int, (Rational, Int)])

/**
  * This is the matchpoint result for one encounter (of NS/EW/Board).
  *
  * @param ns NS pair #
  * @param ew EW pair #
  * @param result the table result
  * @param mp the matchpoints earned by ns for this encounter
  * @param top the maximum number of matchpoints possible
  */
case class Matchpoints(ns: Int, ew: Int, result: PlayResult, mp: Option[Rational], top: Int) {
  def matchesPair(n: Int, dir: Boolean): Boolean = if (dir) n == ns else n == ew
  def getMatchpoints(dir: Boolean): Iterable[Rational] = if (dir) mp else invert
  override def toString = mp match {
    case Some(x) => s"NS: $ns, EW: $ew, score: $result, MP: ${Score.mpsAsString(x,top)}"
    case _ => ""
  }
  private def invert = mp map {r => 1-r}

}

/**
  * This is the traveler for a specific board (in a specific, unnamed, section)
 *
  * @param board number
  * @param ps plays
  */
case class Traveler(board: Int, ps: Seq[Play]) {
  def isPlayed = ps.nonEmpty
  // Calculate the ideal top -- including any Average or DNP scores:
  def top = ps.size - 1
  def matchpointIt: Seq[Matchpoints] = for (p <- ps) yield Matchpoints(p.ns,p.ew,p.result,p.matchpoints(this),top)
  override def toString = {
    val result = StringBuilder.newBuilder
    result.append (s"Board: $board with ${ps.size} plays\n")
    for (m <- matchpointIt) result.append(s"$m\n")
    result.toString
  }
  def matchpoint(x: Play): Option[Rational] = if (isPlayed) {
      val isIs = (for (p <- ps; if p != x; io = p.compare(x.result); i <- io) yield (i,2)) unzip;
      Some(Rational.normalize(isIs._1.sum,isIs._2.sum))
    }
    else None
}

object Traveler {
  def apply(it: Try[Int], ps: Seq[Play]): Traveler = {
    val tt = for (i <- it) yield Traveler(i,ps)
    tt.recover{case x => println(s"Exception: $x");Traveler(0,Seq())}.get
  }
}

/**
  * This is a particular play of an (unspecified) board from an (unspecified) section.
 *
  * @param ns NS pair number
  * @param ew EW pair number
  * @param result the table result
  */
case class Play(ns: Int, ew: Int, result: PlayResult) {
  override def toString = s"NS: $ns, EW: $ew, score: $result"
  def compare(x: PlayResult): Option[Int] = result match {
    case PlayResult(Right(y)) => x match {
      case PlayResult(Right(z)) => Some(Integer.compare(z,y)+1)
      case _ => None
    }
    case _ => None
  }
  def matchpoints(t: Traveler): Option[Rational] = {
    result match {
      case PlayResult(Right(_)) => t.matchpoint(this)
      case PlayResult(Left("A-")) => Some(Rational(2,5))
      case PlayResult(Left("A")) => Some(Rational(1,2))
      case PlayResult(Left("A+")) => Some(Rational(3,5))
      case _ => None // this accounts for the DNP case
    }
  }
}

object Play {
  def apply(ns: Try[Int], ew: Try[Int], result: PlayResult): Play = {
    val z = for (x <- ns; y <- ew) yield Play(x,y,result)
    z.recover{case x => println(s"Exception: $x");Play(0,0,PlayResult.error("no match"))}.get
  }
}

/**
  * This is a play result, that's to say either a bridge score (+ or - according to what NS scored)
  * OR a code.
  *
  * @param r Either: an integer (multiple of 10), Or: one of the following:
  *          DNP: did not play
  *          A+: N/S got Average plus (60%) and E/W Average minus
  *          A: both sides got Average (50%)
  *          A-: N/S got Average minus (40%) and E/W Average plus
  *
  */
case class PlayResult(r: Either[String,Int]) {
  override def toString = r match {
    case Left(x) => x
    case Right(x) => x.toString
  }
}

object PlayResult {
  def apply(s: String): PlayResult = {
    val z = MonadOps.sequence[Int](Try(s.toInt)) match {
      case Left(t) => Left(s) // we ignore the exception because x is probably just a non-integer
      case e@Right(r) => Right(r)
    }
    PlayResult(z)
  }
  def error(s: String) = PlayResult(Left(s))
}

/**
  * RecapParser will parse a String as either an event, section, preamble, pair, traveler, play or result.
  */
class RecapParser extends JavaTokenParsers {
  // XXX event parser yields an Event and is a title followed by a list of sections
  def event: Parser[Event] = sentence~rep(section) ^^ {case p~ss => Event(p,ss)}
  // XXX section parser yields a Section and is a preamble followed by a list of travelers
  def section: Parser[Section] = preamble~rep(traveler) ^^ {case p~ts => Section(p,ts)}
  // XXX (section) preamble parser yields a Preamble (section letter together with pair names) and is a one or two letters followed by a list of pair results
  def preamble: Parser[Preamble] = regex("""[A-Z]{1,2}""".r)~rep(pair) ^^ {case t~ps => Preamble(t,ps)}
  // XXX pair parser yields a Players object and is a number followed by "N" or "E" followed by two full names, each terminated by a period
  def pair: Parser[Players] = wholeNumber~("E"|"N")~sentence~sentence ^^ {case n~d~a~b => Players(n,d,a,b)}
  // XXX traveler parser yields a Traveler object and must start with a "T" and end with a period. In between is a list of Play objects
  def traveler: Parser[Traveler] = "T"~>wholeNumber~rep(play)<~""".""" ^^ {case b~r => Traveler(Try(b.toInt),r)}
  // XXX play parser yields a Play object and must be two integer numbers followed by a result
  def play: Parser[Play] = wholeNumber~wholeNumber~result ^^ { case n~e~r => Play(Try(n.toInt),Try(e.toInt),r) }
  // XXX result parser yields a PlayResult object and must be either a number (a bridge score) or a string such as DNP or A[+-]
  def result: Parser[PlayResult] = (wholeNumber | "DNP" | regex("""A[\-\+]?""".r) ) ^^ { case s => PlayResult(s) }
  // XXX sentence parser recognized a String terminated by a period and yields that String
  def sentence: Parser[String] = regex("""[^\.]+""".r)~""".""" ^^ {case s~p => s}
}