package models

import scala.collection.mutable.{Stack,Map}
import scala.util._

/**
 * Abstract class modeling the "mill" of the calculator.
 * Please note:
 * <ul>
 * <list>Instances of this class are stateful (stack and store) and thus should be invoked only within actors.</list>
 * </list>Methods of this class throw exceptions and therefore should be invoked only within actors.</list>
 * </ul>
 * 
 * CONSIDER making conv implicit
 * 
 * @author scalaprof
 */
abstract class Mill[A : Numeric](stack: Stack[A])(implicit store: Map[String,A]) extends Function1[Valuable[A],Try[A]] { self =>
  
  var debugMill = false;
  def value = if (stack.size>0) Success(stack.top) else Failure(new IllegalArgumentException("stack is empty"))
  def toSeq = stack.toSeq
  def show = println(stack)
  def push(x: A) = { if (debugMill) println(s"push $x");stack.push(x)}
  def pop = {val x = stack.pop; if (debugMill) println(s"popped $x"); x}
  def setDebug(b: Boolean) { debugMill = b }
  def has(n: Int) = assert(stack.size>=n,s"operation requires $n element(s) on stack")
 
  def apply(v: Valuable[A]) = v match {
    case n @ Number(x) => n.apply match {case Success(x) => push(x); case Failure(e) => throw e}; value
    case k @ Constant(x) => k.apply match {case Success(x) => push(x); case Failure(e) => throw e}; value
    case Operator(s) => operate(s); value
    case MemInst(s,n) => memInst(s,n); value
  }
  
  def dyadic(f: (A,A)=>A) = { has(2); push(f(pop,pop)) }
  def monoadic(f: (A)=>A) = { has(1); push(f(pop)) }
  def monoadic2(f: (A,A)=>A)(a: A) = { has(1); push(f(a,pop)) }
  
  def operate(s: String): Unit = s match {
    case "+" => operate("plus")
    case "plus" => dyadic(implicitly[Numeric[A]].plus)
    case "-" => operate("chs"); operate("plus")
    case "chs" => monoadic(implicitly[Numeric[A]].negate)
    case "*" => operate("times")
    case "times" => dyadic(implicitly[Numeric[A]].times)
    case "div" => operate("/")
    case "/" => operate("inv"); operate("times")
    case "inv" => val i = implicitly[Numeric[A]]; if (i.isInstanceOf[Fractional[A]]) monoadic2(i.asInstanceOf[Fractional[A]].div _)(i.one)
    case "swap" => has(2); val (top,next) = (pop,pop); push(top); push(next)
    case "del" => has(1); pop
    case "clr" => stack.clear
    case x => throw new IllegalArgumentException(s"operator $x is not supported")
  }
  
  def memInst(s: String, k: String) = s.toLowerCase match {
    case "sto" => value match {case Success(x) => store.put(k,x); case Failure(e) => throw e}
    case "rcl" => store.get(k) match {case Some(x) => push(x); case None => throw new IllegalArgumentException(s"no value at memory location $k")}
  }
  
  def parse(s: String)(implicit parser: ExpressionParser[A]): Try[A] = 
    parser.parseAll(parser.expr,s) match {
      case parser.Success(ws,_) => try {
          (for (w <- ws) yield apply(w)).reverse.head
        } catch {
        case t: Throwable => Failure(t)
      }
      case parser.Failure(e,_) => Failure(new IllegalArgumentException(s"parseResult error: $e"))
      case r @ _ => Failure(new IllegalArgumentException(s"logic error: parseResult is $r"))
    }
}