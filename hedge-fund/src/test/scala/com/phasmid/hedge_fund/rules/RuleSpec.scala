package com.phasmid.hedge_fund.rules

import org.scalatest.{ WordSpecLike, Matchers, BeforeAndAfterAll, Inside }
import spray.http._

/**
 * This specification really tests much of the HedgeFund app but because it particularly deals with
 * processing data from the YQL (Yahoo Query Language) using JSON, we call it by its given name.
 */
class RuleSpec extends WordSpecLike with Matchers with Inside {

  "Simple Predicate and Candidate" in {
    val predicate = NumberPredicate("x", "<", 3)
    val rule = Rule(predicate)
    rule.apply(MapCandidate("test", Map("x" -> "2"))) should matchPattern {
      case Right(true) =>
    }
    rule.apply(MapCandidate("test", Map("x" -> "4"))) should matchPattern {
      case Right(false) =>
    }
  }

  "Simple Predicate, bad Candidate" in {
    val predicate = NumberPredicate("x", "<", 3)
    val rule = Rule(predicate)
    inside(rule.apply(MapCandidate("test", Map("y" -> "2")))) {
      case Left(x) =>
    }
    inside(rule.apply(MapCandidate("test", Map("x" -> "y")))) {
      case Left(x) =>
    }
  }

  "Simple Rule" in {
    val predicate = Rule("x < 3")
    predicate should matchPattern {
      case NumberPredicate("x", LessThan(), 3) =>
    }
  }
  "Compound Rule" in {
    val predicate = Rule("(x < 3) & (y > 1)")
    predicate should matchPattern {
      case And(NumberPredicate("x", LessThan(), 3), NumberPredicate("y", GreaterThan(), 1)) =>
    }
  }
  "Nested Rule" in {
    val predicate = Rule("(x < 3) & ((y > 1) | (z = 0))")
    predicate should matchPattern {
      case And(
        NumberPredicate("x", LessThan(), 3),
        Or(
          NumberPredicate("y", GreaterThan(), 1),
          NumberPredicate("z", Equals(), 0))) =>
    }
  }
}

