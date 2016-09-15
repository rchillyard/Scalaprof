package edu.neu.coe.scala.ingest

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by scalaprof on 9/13/16.
  */
class MovieSpec extends FlatSpec with Matchers {

  val phi = (math.sqrt(5) + 1) / 2

  behavior of "Principal"

  it should "work for String, Int" in {
    val x = Principal("Tom Brady", 1)
    x should matchPattern {
      case Principal("Tom", None, "Brady", None, 1) =>
    }
  }
  it should "work for String, String, Int" in {
    val x = Principal("Tom", None, "Brady", None, 1)
    x should matchPattern {
      case Principal("Tom", None, "Brady", None, 1) =>
    }
  }
  it should "work for List[String]" in {
    Principal(List("Tom Brady", "1")) should matchPattern {
      case Principal("Tom", None, "Brady", None, 1) =>
    }
    Principal(List("Noémie Lenoir", "2")) should matchPattern {
      case Principal("Noémie", None, "Lenoir", None, 2) =>
    }
    Principal(List("J.J. Abrams", "3")) should matchPattern {
      case Principal("J.", Some("J."), "Abrams", None, 3) =>
    }
    Principal(List("Robert Downey Jr.", "4")) should matchPattern {
      case Principal("Robert", None, "Downey", Some("Jr."), 4) =>
    }

  }

  behavior of "Rating"

  it should "work for String, Int" in {
    val x = Rating("PG", Some(13))
    x should matchPattern {
      case Rating("PG", Some(13)) =>
    }
  }
  it should "work for PG-13" in {
    val x = Rating("PG-13")
    x should matchPattern {
      case Rating("PG", Some(13)) =>
    }
  }
  it should "work for R" in {
    val x = Rating("R")
    x should matchPattern {
      case Rating("R", None) =>
    }
  }

  behavior of "Format"

  it should "work for Boolean, String, Double, Int" in {
    val x = Format(color = true, "Swahili", phi, 129)
    x should matchPattern {
      case Format(true, "Swahili", `phi`, 129) =>
    }
  }
  it should "work for List[String]" in {
    val x = Format(List("Color", "Swahili", phi.toString, "129"))
    x should matchPattern {
      case Format(true, "Swahili", `phi`, 129) =>
    }
  }

  behavior of "Production"

  it should "work for String, Int" in {
    val x = Production("Kenya", 1000000, 1000001, 2016)
    x should matchPattern {
      case Production("Kenya", 1000000, 1000001, 2016) =>
    }
  }
  it should "work for List[String]" in {
    val x = Production(List("Kenya", "1000000", "1000001", "2016"))
    x should matchPattern {
      case Production("Kenya", 1000000, 1000001, 2016) =>
    }
  }

  behavior of "Reviews"

  it should "work for params" in {
    val x = Reviews(8.14, 42, Rating("PG-13"), 7, 10, 12, 99)
    x should matchPattern {
      case Reviews(8.14, 42, Rating("PG", Some(13)), 7, 10, 12, 99) =>
    }
  }
  it should "work for List[String]" in {
    val x = Reviews(List("8.14", "42", "PG-13", "7", "10", "12", "99"))
    x should matchPattern {
      case Reviews(8.14, 42, Rating("PG", Some(13)), 7, 10, 12, 99) =>
    }
  }
}
