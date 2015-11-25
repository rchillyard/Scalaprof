package models

import scala.collection.mutable.Stack

/**
 * @author scalaprof
 */
case class Mill(stack: Stack[Double], parser: ExpressionParser) extends Function1[String,Double] {
  
  def value = stack.top 
  def toSeq = stack.toSeq
  def show = println(stack)
  def push(x: Double) = {println(s"push $x"); stack.push(x)}
  def pop = stack.pop
  def has(n: Int) = assert(stack.size>=n,s"operation requires $n element(s) on stack")
  def fromString(s: String): Option[Double] = 
    try {Some(s.toDouble)}
  catch {
    case t: Throwable => None
  }
  
  def present(s: String) {
    fromString(s) match {
      case Some(x) => push(x)
      case None => operate(s)
    }
  }
  
  
  def dyadic(f: (Double,Double)=>Double) = { has(2); push(f(pop,pop)) }
  def monoadic(f: (Double)=>Double) = { has(1); push(f(pop)) }
  def monoadic2(f: (Double,Double)=>Double)(a: Double) = { has(1); push(f(a,pop)) }
  
  
    def operate(s: String) = s match {
    case "+" => apply("plus")
    case "plus" => dyadic(implicitly[Numeric[Double]].plus)
    case "-" => apply("neg plus")
    case "neg" => monoadic(implicitly[Numeric[Double]].negate)
    case "*" => apply("times")
    case "times" => dyadic(implicitly[Numeric[Double]].times)
    case "/" => apply("inv times")
    case "inv" => val i = implicitly[Numeric[Double]]; if (i.isInstanceOf[Fractional[Double]]) monoadic2(i.asInstanceOf[Fractional[Double]].div _)(i.one)
    case "swap" => has(2); val (top,next) = (pop,pop); push(top); push(next)
    case "del" => has(1); pop
    case x => throw new IllegalArgumentException(s"operator $x is not supported")
  }
  def apply(x: String): Double = {
    parser.parseAll(parser.expr, x) match {
      case parser.Success(x,_) => for (s <- x) present(s); value
      case parser.Failure(_,_) => println("cry"); 0.0
      case _ => println("cry"); 0.0
    }
  }
  
}