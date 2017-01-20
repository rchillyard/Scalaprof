package com.phasmid.hedge_fund.rules

/**
 * @author robinhillyard
 */
trait Predicate extends Function1[Candidate, Either[Throwable, Boolean]]

object Predicate {
  def apply(predicate: String): Predicate = {
    val rNumPredicate = """^\s*(\w+)\s*([=<>]{1,2})\s*(-?[0-9]+\.?[0-9]*)\s*$""".r
    val rTextPredicate = """^\s*(\w+)\s*([=<>]{1,2})\s*(\w+)\s*$""".r
    predicate match {
      case "Always" => Always()
      case "Never" => Never()
      case rNumPredicate(v, o, n) => NumberPredicate.apply(v, o, n)
      case rTextPredicate(v, o, n) => StringPredicate.apply(v, o, n)
      case _ => throw new Exception(s"predicate: $predicate is malformed")
    }
  }
}

case class Always() extends Predicate {
  def apply(candidate: Candidate) = Right(true)
}
case class Never() extends Predicate {
  def apply(candidate: Candidate) = Right(false)
}
