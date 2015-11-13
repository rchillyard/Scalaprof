package edu.neu.coe.scala

object Factorial {
  println("Welcome to the Factorial worksheet")   //> Welcome to the Factorial worksheet
  def factorial(x: Long): Long = if (x==0) 1 else x * factorial(x-1)
                                                  //> factorial: (x: Long)Long
  factorial(20)                                   //> res0: Long = 2432902008176640000
}