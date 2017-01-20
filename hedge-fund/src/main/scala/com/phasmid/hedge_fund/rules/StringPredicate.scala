package com.phasmid.hedge_fund.rules

import scala.util.Try
import scala.util._

import scala.util.Failure

/**
 * @author robinhillyard
 */
case class StringPredicate(variable: String, operator: Operator[String], value: String) extends Predicate {

  def apply(candidate: Candidate): Either[Throwable, Boolean] = candidate(variable) match {
    case Some(x) => Try { operator(x.toString, value) } match {
      case Success(v) => Right(v)
      case Failure(f) => Left(f)
    }
    case _ => Left(new Exception(s"variable $variable not found in $candidate"))
  }
}

object StringPredicate {
  def apply(variable: String, operator: String, value: String): StringPredicate =
    new StringPredicate(variable, Operator.createText(operator), value)
}