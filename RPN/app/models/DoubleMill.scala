package models

import scala.collection.mutable.{Stack,Map}
import scala.util._

/**
 * @author scalaprof
 */
class DoubleMill(stack: Stack[Double], store: Map[String,Double], conv: String=>Try[Double]) extends Mill[Double](stack, store, DoubleMill.constants.get _, conv) {
  
    def apply(s: String): Try[Double] =
      try {
        Success(s.toDouble)
      }
    catch {
      case t: Throwable => Failure(t)
    }
  
}

object DoubleMill {
  def apply() = new DoubleMill(Stack[Double](), scala.collection.mutable.Map[String,Double](), {s => Try(s.toDouble)})
  val constants = Map("e"->math.E, "pi"->math.Pi)
}