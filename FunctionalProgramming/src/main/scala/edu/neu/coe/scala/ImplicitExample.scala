package edu.neu.coe.scala

class ImplicitExample {
}
object ImplicitExample extends App {
  def cToF(x: Double): Double = x*9/5+32
  val x = cToF(10)
}