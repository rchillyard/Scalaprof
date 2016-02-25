package models

import scala.collection.mutable.{Stack,Map}
import scala.util._
import spire.math._
import spire.implicits._

/**
 * @author scalaprof
 */
object SpireMill {

  val conv: String=>Try[Real] = SpireMill.valueOf _
  val lookup: String=>Option[Real] = SpireMill.constants.get _
  implicit val store = Map[String,Real]()
  implicit val parser = new ExpressionParser[Real](conv,lookup)
  def apply(): Mill[Real] = new MillSpire(Stack[Real]()) {
    def apply(s: String): Try[Real] = SpireMill.valueOf(s)    
  }
  def valueOf(s: String): Try[Real] = Try(Real(s))
 val constants = Map("e"->Real.e, "pi"->Real.pi)
}