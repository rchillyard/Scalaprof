package edu.neu.coe.scala

/**
 * The trial package provides types to aid in the composition of functions of the form X=>Try[Y].
 * Of course, all functions can be composed, but normal functional composition gives us something like h(x) = g(f(x)).
 * That's not what we want here.
 * 
 * @author scalaprof
 */
package object trial {
    
  import scala.util._

  /**
   * alias for a X=>Try[Y]
 	 */
 	type Trial[-X,+Y] = Function1[X,Try[Y]]

	/**
	 * Trait ComposableTrial which is a function X=>Try[Y] which can be composed with other X=>Try[Y] functions.
	 * The order of trials in each case is the left-most parameter first, followed by whatever is on the right.
	 * 
	 * @author scalaprof
	 *
	 * @param <X> the type of the input to this function
	 * @param <Y> the underlying type of the Try output of this function
	 */
	trait ComposableTrial[-X,+Y] extends Trial[X,Y] {
		def :|[P <: X,Q >: Y](f: Trial[P,Q]) = new ComposedTrial[P,Q](this,f)
		def :||[P <: X,Q >: Y](fs: Seq[Trial[P,Q]]): Trial[P,Q] = ??? // TODO Assignment6 11
	}

	/**
	 * Case class ComposedTrial which extends ComposableTrial.
	 * This function is the composition of two X=>Try[Y] functions provided in the constructor parameters.
	 * The result of applying the function is to try the "first" function (on the left) first.
	 * If that results in a success, then we return it.
	 * Otherwise, we try the "second" function.
	 *
	 * @param <X> the type of the input to this function
	 * @param <Y> the underlying type of the Try output of this function
	 */
  class ComposedTrial[-X,+Y](first: Trial[X,Y], second: Trial[X,Y]) extends ComposableTrial[X,Y] {
    def this(f: Trial[X,Y]) = this(Identity(),f)
  	def apply(x: X): Try[Y] = ??? // TODO Assignment6 5
  }

  /**
   * Case class First which extends ComposedTrial.
   * This function is simply a wrapper around the function f and is functionally equivalent to:
   * <code>Identity :| f
   * </code>
   * The purpose of this class is to provide an initial trial function which can be composed with other trial functions using :| or :||
   *
   * @param <X> the type of the input to this function
   * @param <Y> the underlying type of the Try output of this function
   */
  case class First[-X,+Y](f: X=>Try[Y]) extends ComposedTrial[X,Y](f)

  /**
   * Case class Identity which extends ComposableTrial.
   * This function always results in a failure, which is to say that it is essentially ignored when composed with other trials.
   * The purpose of this class is to provide an initial trial function which can be composed with other trial functions using :| or :||
   *
   * @param <X> the type of the input to this function
   * @param <Y> the underlying type of the Try output of this function
   */
  case class Identity[-X, +Y]() extends ComposableTrial[X,Y] {
  	def apply(x: X): Try[Y] = Failure(TrialException("identity"))
  }
  
  /**
   * Case class Sequence which extends ComposedTrial.
   * This function always results in a failure, which is to say that it is essentially ignored when composed with other trials.
   * The purpose of this class is to provide an initial trial function which can be composed with other trial functions using :| or :||
   *
   * @param <X> the type of the input to this function
   * @param <Y> the underlying type of the Try output of this function
   */
  case class Sequence[-X, +Y](ts: Seq[Trial[X,Y]]) extends ComposedTrial[X,Y](Identity() :|| ts)

  /**
   * Case class CurriedTrial which extends ComposedTrial.
   * This result of applying this function to an input x is to apply the function defined by
   * applying parameter g to the parameter w. 
   * The purpose of this class is to allow the behavior of the function applied to be varied according
   * to the given parameter w. 
   *
   * @param <W> the type of the parameter which is the second parameter of the constructor and which
   * will be applied to the function g.
   * @param <X> the type of the input to this function
   * @param <Y> the underlying type of the Try output of this function
   * @param g a curried trial, of type W=>Trial[X,Y]
   * @param w the corresponding parameter to be applied to g
   */
  case class CurriedTrial[-W,-X,+Y](g: W=>X=>Try[Y])(w: W) extends ComposedTrial[X,Y](g(w))

  /**
   * Case class Sequence which extends ComposedTrial.
   * This function always results in a failure, which is to say that it is essentially ignored when composed with other trials.
   * The purpose of this class is to provide an initial trial function which can be composed with other trial functions using :| or :||
   *
   * @param <W> the type of the parameter which is the second parameter of the constructor and which
   * will be applied to the function g.
   * @param <X> the type of the input to this function
   * @param <Y> the underlying type of the Try output of this function
   * @param gs a sequence of curried trials, each of type W=>Trial[X,Y]
   * @param ws a sequence of corresponding parameters to be applied to those curried trials
   */
  case class CurriedSequence[V >: W, -W,-X,+Y](gs: Seq[V=>Trial[X,Y]])(ws: Seq[W]) extends ComposedTrial[X,Y](Sequence((gs zip ws) map {case (g,w) => CurriedTrial[W,X,Y](g)(w)}))

  
  case class TrialException(m: String) extends Exception(m)
}