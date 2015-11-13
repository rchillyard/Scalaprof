package edu.neu.coe.scala
package newton

import scala.annotation.tailrec

/**
 * @author scalaprof
 * (c) Robin Hillyard (2015)
 */
case class Newton(f: Newtonian, guess: Double, maxTries: Int) {
	def solve: Either[String,Double] = solve(State(guess,maxTries))
	private def step(s: State): Either[State,Either[String,Double]] =
		f(s) match {
		  case None => Right(Right(s.x))
			case Some(x) => s(x) match {
			  case Left(e) => Right(Left(e))
			  case Right(s1) => Left(s1)
		}
  }
  @tailrec private def solve(s: State): Either[String,Double] =
    step(s) match {
      case Right(r) => r
      case Left(l) => solve(l)
    }
}

case class State(x: Double, tries: Int) extends Function1[Double,Either[String,State]] {
	def apply(x: Double) = tries match {
	  case 0 => Left("Failed to converge")
	  case _ => Right(State(x,tries-1))
	}
}

case class Newtonian(name: String, f: Double=>Double, dfbdx: Double=>Double, threshold: Double) extends Function1[State,Option[Double]] {
	def apply(s: State) = {
    val x = s.x
	  val y = f(x)
		if (math.abs(y) > threshold)
			Some(x - y/dfbdx(x))
		else
			None
	}
}

object Newton {
	def apply(f: Newtonian, start: Double): Newton = apply(f,start,100)

	def main(args: Array[String]): Unit = {
		val f = Newtonian("cos(x)-x (~1E-7)", {x => math.cos(x) - x},{x => -math.sin(x) - 1},1E-7)
		Newton(f,1.0).solve match {
			case Right(x) => println(s"""the solution to "${f.name}" is $x""")
			case Left(m) => println(s"error: $m")
		}
	}
}
