package edu.neu.coe.csye._7200

import scala.language.implicitConversions
;

/**
 * @author scalaprof
 */
object Implicits extends App {
  val x = "1" + 2
  println(x)

  def myAdd(x: Int, y: Int): Int = x+y

  implicit def stringToInt(x: String) = x.toInt

  myAdd("1","2")

}
