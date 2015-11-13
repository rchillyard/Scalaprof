package edu.neu.coe.scala
package factorial

import scala.annotation.tailrec

/**
 * @author scalaprof
 */
class Factorial(val n: Int) extends AnyVal {
  def getValue: Long = {
    import scala.annotation.tailrec
    @tailrec def inner(n: Int, r: Long): Long = if (n<=1) r else inner(n-1,n*r)
    inner(n,1)
  }
}

object Factorial extends App {
  println(new Factorial(5).getValue)
}