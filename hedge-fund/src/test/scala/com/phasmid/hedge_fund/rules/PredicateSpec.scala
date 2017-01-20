package com.phasmid.hedge_fund.rules

import org.scalatest.{ WordSpecLike, Matchers, BeforeAndAfterAll, Inside }
import spray.http._

/**
 * This specification really tests much of the HedgeFund app but because it particularly deals with
 * processing data from the YQL (Yahoo Query Language) using JSON, we call it by its given name.
 */
class PredicateSpec extends WordSpecLike with Matchers with Inside {

  "Simple Predicate and Candidate" in {
    val predicate = NumberPredicate("x", "<", 3)
    predicate.apply(MapCandidate("test", Map("x" -> "2"))) should matchPattern {
      case Right(true) =>
    }
    predicate.apply(MapCandidate("test", Map("x" -> "4"))) should matchPattern {
      case Right(false) =>
    }
  }

  "Simple Predicate, bad Candidate" in {
    val predicate = NumberPredicate("x", "<", 3)
    inside(predicate.apply(MapCandidate("test", Map("y" -> "2")))) {
      case Left(x) =>
    }
    inside(predicate.apply(MapCandidate("test", Map("x" -> "y")))) {
      case Left(x) =>
    }
  }

  "String Predicate" in {
    val predicate = Predicate("x < 3")
    predicate should matchPattern {
      case NumberPredicate("x", LessThan(), 3) =>
    }
    predicate shouldEqual NumberPredicate("x", "<", 3)
  }

  "Text Predicate" in {
    val predicate = Predicate("x == Hello")
    predicate.apply(MapCandidate("test", Map("x" -> "Hello"))) should matchPattern {
      case Right(true) =>
    }
  }
}

