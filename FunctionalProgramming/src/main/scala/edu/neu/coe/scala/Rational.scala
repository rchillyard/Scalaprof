package edu.neu.coe.scala

import scala.math.pow

/**
 * @author scalaprof
 */
case class Rational(n: Long, d: Long) extends Numeric[Rational] {
  
  // Pre-conditions
  require(Rational.gcd(n,d)==1,s"Rational($n,$d): arguments have common factor")
  
  // Operators
  def + (that: Rational): Rational = plus(this,that)
  def + (that: Long): Rational = this + Rational(that)
  def - (that: Rational): Rational = minus(this,that)
  def - (that: Long): Rational = this - Rational(that)
  def * (that: Rational): Rational = times(this,that)
  def * (that: Long): Rational = this * Rational(that)
  def / (that: Rational): Rational = this * that.invert
  def / (that: Long): Rational = this / Rational(that)
  def ^ (that: Int): Rational = power(that)
  
  // Members declared in scala.math.Numeric
  def fromInt(x: Int) = Rational.apply(x)
  def minus(x: Rational,y: Rational): Rational = plus(x,negate(y))
  def negate(x: Rational): Rational = Rational(-n,d)
  def plus(x: Rational,y: Rational): Rational = Rational.normalize(x.n*y.d+x.d*y.n,x.d*y.d)
  def times(x: Rational,y: Rational): Rational = Rational.normalize(x.n*y.n, x.d*y.d)
  def toDouble(x: Rational): Double = x.n*1.0d/x.d
  def toFloat(x: Rational): Float = toDouble(x).toFloat
  def toInt(x: Rational): Int = {val l = toLong(x); if (Rational.longAbs(l)<Int.MaxValue) l.toInt else throw new Exception(s"$x is too big for Int")}
  def toLong(x: Rational): Long = if (x.isWhole) x.n else throw new Exception(s"$x is not Whole")
  
  // Members declared in scala.math.Ordering
  def compare(x: Rational,y: Rational): Int = minus(x,y).n.signum
  
  // Other methods appropriate to Rational
  def invert = Rational(d,n)
  def isWhole = d==1L
  def isZero = n==0L
  def isUnity = n==1L && isWhole
  def isInfinity = d==0L
  def toInt: Int = toInt(this)
  def power(x: Int) = Rational(Rational.longPow(n,x),Rational.longPow(d,x))
  def toBigDecimal = BigDecimal(n)/d
}


object Rational {

	implicit class RationalHelper(val sc: StringContext) extends AnyVal {
		def r(args: Any*): Rational = {
			val strings = sc.parts.iterator
			val expressions = args.iterator
			val sb = new StringBuffer()
			while(strings.hasNext) {
				val s = strings.next
				if (s.isEmpty) {
				  if(expressions.hasNext)
					  sb.append(expressions.next)
          else
            throw new Exception("r: logic error: missing expression")
				}
				else
				  sb.append(s)
			}
			if(expressions.hasNext)
			  throw new Exception(s"r: ignored: ${expressions.next}")
			else
				Rational(sb.toString)
		}
	}
  
  val ZERO = Rational(0)
  val INFINITY = ZERO.invert
  val ONE = Rational(1)
  val TEN = Rational(10)
  val HALF: Rational = Rational("1/2")
  
  def apply(x: Long): Rational = new Rational(x,1)
  def apply(x: String): Rational = {
    val rRat = """^\s*(\d+)\s*(\/\s*(\d+)\s*)?$""".r
    x match {
      case rRat(n,_,d) => normalize(n.toLong,d.toLong)
      case rRat(n) => Rational(n.toLong)
      case _ => throw new Exception(s"invalid rational expression: $x")
    }
  }
  def normalize(n: Long, d: Long) = {
    val g = gcd(n,d)
    apply(n/g,d/g)
  }
  
import scala.annotation.tailrec
  @tailrec private def gcd(a: Long, b: Long): Long = if (b==0) a else gcd(b, a % b)
  private def longPow(a: Long, b: Int): Long = Iterator.iterate(1L)({_ * a}).drop(b).next
  private def longAbs(a: Long): Long = if (a < 0) -a else a
  
}