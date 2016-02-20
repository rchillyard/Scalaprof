package edu.neu.coe.scala.parse

import edu.neu.coe.scala.numerics.Rational
import scala.util.Try
import edu.neu.coe.scala.numerics.Fuzzy

/**
 * @author scalaprof
 */
abstract class ExpressionParserNumeric[T : Numeric](implicit num: Numeric[T]) extends ExpressionParser[T] { self =>

  def div: (T,T)=>T = if (num.isInstanceOf[Fractional[T]]) (num.asInstanceOf[Fractional[T]]).div else throw new IllegalArgumentException("div method unavailable")
  def one: T = num.one
  def zero: T = num.zero
  def negate: (T)=>T = num.negate
  def plus: (T,T)=>T = num.plus
  def times: (T,T)=>T = num.times
}

object DoubleExpressionParser extends ExpressionParserNumeric[Double] {
  def apply(s: String): Try[Double] = Try(s.toDouble)
}
object IntExpressionParser extends ExpressionParserNumeric[Int] {
  def apply(s: String): Try[Int] = Try(s.toInt)
}
object RationalExpressionParser extends ExpressionParserNumeric[Rational] {
  def apply(s: String): Try[Rational] = Try(Rational.apply(s))
}
//object FuzzyExpressionParser extends ExpressionParserNumeric[Fuzzy] {
//  def apply(s: String): Try[Fuzzy] = Try(Fuzzy.apply(s))
//}