package models

import scala.collection.mutable.{Stack,Map}
import scala.util._
import spire.math._
import spire.implicits._

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
abstract class MillSpire[A : Numeric](stack: Stack[A])(implicit store: Map[String,A]) extends Mill[A](stack)(store) { self =>
  
  def operate(s: String): Unit = s match {
    case "+" => operate("plus")
    case "plus" => dyadic(implicitly[Numeric[A]].plus _)
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
  
}