package models

import scala.util.parsing.combinator.JavaTokenParsers
import scala.util.Try

/**
 * @author scalaprof
 */
class ExpressionParser extends JavaTokenParsers {
  
  def expr: Parser[List[String]] = repsep(term," ")
  def term: Parser[String] = op | value
  def op: Parser[String] = ident | "+" | "-" | "*" | "/"
  def value: Parser[String] = floatingPointNumber
}
