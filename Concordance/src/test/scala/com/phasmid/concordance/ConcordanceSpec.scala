package com.phasmid.concordance

import org.scalatest.{ FlatSpec, Matchers }

/**
 * @author scalaprof
 * (c) Phasmid Software, 2015
 */
class ConcordanceSpec extends FlatSpec with Matchers {
  "Concordance" should "read Hello World!" in {
    val p = new ConcordanceParser
    val r = p.parseAll(p.sentence,"Hello World!") match {
      case p.Success(ws,_) => ws
      case p.Failure(e,_) => println(e); List()
      case _ => println("PositionalParser: logic error"); List()
    }

    r should matchPattern { case h::tail => }
    r.head should matchPattern { case (i, s) => }
    r.head should matchPattern { case (1, "Hello") => }
    r.tail.head should matchPattern { case (7, "World!") => }
  }
}
