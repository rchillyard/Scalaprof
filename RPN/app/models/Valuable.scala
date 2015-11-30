package models

import scala.util._

/**
 * A Valuable[A] is a virtual A
 * 
 * @author scalaprof
 */
trait Valuable[A]
  
case class Number[A : Numeric](s: String)( conv: String=>Try[A]) extends Valuable[A] with Function0[Try[A]] {
  def apply = conv(s)
  override def toString = apply.toString+"("+s+")"
}

case class Operator[A : Numeric](s: String) extends Valuable[A] {
    override def toString = s
}

case class MemInst[A : Numeric](s: String, k: String) extends Valuable[A] {
  override def toString = s+":"+k
}

case class Constant[A : Numeric](s: String)( lookup: String=>Option[A]) extends Valuable[A] {
  def apply = lookup(s) match {
    case Some(x) => Success(x)
    case None => Failure(new IllegalArgumentException(s"lookup failed for $s"))
  }
  override def toString = s
}
