package edu.neu.coe.scala
package sorting

/**
 * @author scalaprof
 */
object Sorting {
  
  val a = List(10, 5, 8, 1, 7).sorted
  
  val r = List(Rational(1,2),Rational(2,3),Rational(1,3))
  
  import Rational.ord
//  val s =  r.sorted
}

  case class Rational (n: Int, d: Int) {
    def value = n.toDouble / d
  }
  
object Rational {
    implicit val ord = new RationalOrdering{
      def compare(x: Rational, y: Rational) = x.value compare(y.value)
    }
  }
  
trait RationalOrdering extends Ordering[Rational]

