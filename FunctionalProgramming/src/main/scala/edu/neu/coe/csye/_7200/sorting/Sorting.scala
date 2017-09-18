package edu.neu.coe.csye._7200.sorting

/**
 * @author scalaprof
 */
trait RationalOrdering extends Ordering[Rational]
object Sorting extends App {
  
  val a = List(10, 5, 8, 1, 7).sorted
  
  val r = List(Rational(1,2),Rational(2,3),Rational(1,3))
//  val s =  r.sorted
  
  println(a)
//  println(r.sorted)
}

  case class Rational (n: Int, d: Int) {
    def value = n.toDouble / d
  }
  
object Rational {
    implicit val ord = new RationalOrdering{
      def compare(x: Rational, y: Rational) = x.value compare(y.value)
    }
  }
  

