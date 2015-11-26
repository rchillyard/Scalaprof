package models

import scala.util.parsing.combinator.JavaTokenParsers
import scala.util.Try
import scala.math.ScalaNumericConversions


/**
 * @author scalaprof
 */
class ExpressionParser[A : Numeric](implicit conv: String=>Try[A], lookup: String=>Option[A]) extends JavaTokenParsers { self =>
    
  def expr: Parser[List[Valuable[A]]] = rep(term)
  def term: Parser[Valuable[A]] = (meminst | value | const | op)
  def meminst: Parser[Valuable[A]] = ("sto" | "rcl")~":"~ident ^^ { case s~":"~k => MemInst(s,k) }
  def op: Parser[Valuable[A]] = (ident | "+" | "-" | "*" | "/") ^^ { x => Operator(x) }
  def const: Parser[Valuable[A]] = "_"~>ident ^^ { case s => Constant(s) }
  def value: Parser[Valuable[A]] = floatingPointNumber ^^ { x => Number(x) }

}