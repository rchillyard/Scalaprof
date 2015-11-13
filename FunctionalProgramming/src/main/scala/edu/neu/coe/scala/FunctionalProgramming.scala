package edu.neu.coe.scala

import scala.collection.GenTraversableOnce


object FunctionalProgramming {
  
  def evaluate_3_tenths = 1.0/10 + 2.0/10
  
  def multiply_by_10_over_3(x: Double) = x / 3 * 10
  
  def main(args: Array[String]): Unit = {
    val x = evaluate_3_tenths
    val y = multiply_by_10_over_3(x)
    println(y + " != 1");
  }
}