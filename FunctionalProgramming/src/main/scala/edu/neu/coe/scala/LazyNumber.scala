package edu.neu.coe.scala

/**
  * Created by scalaprof on 9/30/16.
  */
trait LazyNumber[T] extends (()=>T) {
//  def unit[U](x: => T): LazyNumber[U]
  def flatMap[U](g: T=>LazyNumber[U]): LazyNumber[U] = g(apply())
//  def map[U](g: T=>U): LazyNumber[U] = flatMap {x => unit(x)}
}

class LazyInt(x: => Int, f: Int=>Int) extends LazyNumber[Int] {
  def apply(): Int = f(x)

//  def unit[U](x: => Int): LazyNumber[U] = new LazyInt(x,identity)
}
