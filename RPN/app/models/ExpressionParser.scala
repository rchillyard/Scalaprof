package models

import scala.util.parsing.combinator.JavaTokenParsers
import scala.util.Try

/**
 * @author scalaprof
 */
case class ExpressionParser[A : Numeric](conv: String=>Try[A], lookup: String=>Option[A]) extends JavaTokenParsers { self =>
    
  def expr: Parser[List[Valuable[A]]] = rep(term)
  def term: Parser[Valuable[A]] = (meminst | value | const | op | failure("bad term"))
  def meminst: Parser[Valuable[A]] = ("sto" | "rcl" | failure("mem inst"))~":"~ident ^^ { case s~":"~k => MemInst(s,k) }
  def op: Parser[Valuable[A]] = (ident | "+" | "-" | "*" | "/" | failure("no op")) ^^ { x => Operator(x) }
  def const: Parser[Valuable[A]] = "_"~>ident ^^ { case s => Constant(s)(lookup) }
  def value: Parser[Valuable[A]] = floatingPointNumber ^^ { x => Number(x)(conv) }
  override def toString = s"""ExpressionParser with conv(1) = ${conv("1")}, conv = ${conv}, lookup(pi) = ${lookup("pi")}"""

}