package models

import scala.collection.mutable.{Stack,Map}
import scala.util._
import edu.neu.coe.scala.numerics.Rational

/**
 * @author scalaprof
 */
object RationalMill {

  val conv: String=>Try[Rational] = RationalMill.valueOf _
  val lookup: String=>Option[Rational] = RationalMill.constants.get _
  implicit val store = Map[String,Rational]()
  implicit val parser = new ExpressionParser[Rational](conv,lookup)
  def apply(): Mill[Rational] = new Mill(Stack[Rational]()) {
    def apply(s: String): Try[Rational] = RationalMill.valueOf(s)    
  }
  def valueOf(s: String): Try[Rational] = Try(Rational(s))
 val constants = Map("e"->Rational(BigDecimal(math.E)), "pi"->Rational(BigDecimal(math.Pi)))
}