package com.phasmid.hedge_fund.rules

/**
 * @author robinhillyard
 */
case class Rule(predicate: Predicate) extends Predicate {
  // TODO don't think we need to override this here
  def apply(candidate: Candidate): Either[Throwable, Boolean] = predicate.apply(candidate)
}

object Rule {
  // Note that this expression is tail-recursive. TODO [I don't think so!]
  // That's to say that parentheses can be nested, provided that all adjacent closing parens
  // appear at the termination of the string.
  val rRule = """^\(([^\)]+)\)\s*(\&|\|)\s*\((.+)\)$""".r
  def apply(s: String): Predicate = s match {
    case rRule(p1, "&", p2) => And(Predicate(p1), Rule(p2))
    case rRule(p1, "|", p2) => Or(Predicate(p1), Rule(p2))
    case _ => Predicate(s)
  }
}

case class And(p1: Predicate, p2: Predicate) extends Predicate {
  def apply(candidate: Candidate) = p1.apply(candidate) match {
    case Right(x) => if (x) p2.apply(candidate) else Right(false)
    case x => x
  }
}
case class Or(p1: Predicate, p2: Predicate) extends Predicate {
  def apply(candidate: Candidate) = p1.apply(candidate) match {
    case Right(x) => if (x) Right(true) else p2.apply(candidate)
    case x => x
  }
}