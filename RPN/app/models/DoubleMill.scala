package models

import scala.collection.mutable.{Stack,Map}
import scala.util._
//
/**
 * @author scalaprof
 */
object DoubleMill {
  val conv: String=>Try[Double] = DoubleMill.valueOf _
  val lookup: String=>Option[Double] = DoubleMill.constants.get _
  implicit val store = Map[String,Double]()
  implicit val parser = new ExpressionParser[Double](conv,lookup)
  def apply(): Mill[Double] = new Mill(Stack[Double]()) {
    def apply(s: String): Try[Double] = DoubleMill.valueOf(s)    
  }
  def valueOf(s: String): Try[Double] = Try(s.toDouble)
  val constants = Map("e"->math.E, "pi"->math.Pi)
}