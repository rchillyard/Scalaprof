package models

import scala.collection.mutable.{Stack,Map}
import scala.util._
import edu.neu.coe.scala.numerics.Rational

/**
 * @author scalaprof
 */
class RationalMill(stack: Stack[Rational], store: Map[String,Rational], conv: String=>Try[Rational]) extends Mill[Rational](stack, store, RationalMill.constants.get _, conv) {
  
    def apply(s: String): Try[Rational] =
      try {
        Success(Rational(s))
      }
    catch {
      case t: Throwable => Failure(t)
    }
  
}

object RationalMill {
  def apply() = new RationalMill(Stack[Rational](), scala.collection.mutable.Map[String,Rational](), {s => Try(Rational(s))})
  val constants = Map("e"->Rational(BigDecimal(math.E)), "pi"->Rational(BigDecimal(math.Pi)))
}