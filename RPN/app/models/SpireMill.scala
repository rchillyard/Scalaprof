package models

import scala.collection.mutable.{Stack,Map}
import scala.util._
import spire.math._
import spire.implicits._

/**
 * @author scalaprof
 */
object SpireMill {

  val conv: String=>Try[Rational] = SpireMill.valueOf _
  val lookup: String=>Option[Rational] = SpireMill.constants.get _
  implicit val store = Map[String,Rational]()
  implicit val parser = new ExpressionParser[Rational](conv,lookup)
  def apply(): Mill[Rational] = new MillSpire(Stack[Rational]()) {
    def apply(s: String): Try[Rational] = SpireMill.valueOf(s)    
  }
  def valueOf(s: String): Try[Rational] = Try(Rational(s))
 val constants = Map("e"->Rational(BigDecimal(math.E)), "pi"->Rational(BigDecimal(math.Pi)))
}