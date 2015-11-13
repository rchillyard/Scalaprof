package edu.neu.coe.scala.mapreduce

object Currying {
  println("Welcome to the Currying worksheet")
  
  val matrix = List(List(1,2,3),List(2,3,1),List(3,1,2))
  def element(r: Int)(c: Int) = matrix(r)(c)
  
  val r = 0
  
  def g(c: Int) = matrix(r)(c)
  
  def row(r: Int) = g
  
}