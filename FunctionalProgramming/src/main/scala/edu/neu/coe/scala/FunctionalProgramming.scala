package edu.neu.coe.scala

import scala.collection.GenTraversableOnce


object FunctionalProgramming extends App {
  
  def evaluate_3_tenths = 1.0/10 + 2.0/10

  val a = 42
  val f = { x: Int => x*x }
  def m(x: Int) = x*x
  
  def multiply_by_10_over_3(x: Double) = x / 3 * 10
  
    val x = evaluate_3_tenths
    val y = multiply_by_10_over_3(x)
    println(y + " != 1");
}

class MyClass(x: Int) {
  def sqr = x*x
}