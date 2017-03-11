package edu.neu.coe.scala
package factorial

import scala.annotation.tailrec

/**
  * @author scalaprof
  */
class Factorial(val n: Int) extends (()=>Long) {
  def apply: Long = Factorial.factorial(n)
}

object Factorial extends App {
  println(new Factorial(5)())

  def factorial(n: Int) = {
    @tailrec def inner(r: Long, n: Int): Long =
      if (n <= 1) r
      else inner(n * r, n - 1)
    inner(1L, n)
  }
}