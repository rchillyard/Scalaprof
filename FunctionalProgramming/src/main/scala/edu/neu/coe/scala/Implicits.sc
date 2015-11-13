package edu.neu.coe.scala
package implicits

object Implicits {
  println("Welcome to the Implicits worksheet")   //> Welcome to the Implicits worksheet
  
  implicit def stringToInt(x: String) = x.toInt;  //> stringToInt: (x: String)Int
  
  def add(x: Int, y: Int)(implicit z: Int): Int = x+y+z
                                                  //> add: (x: Int, y: Int)(implicit z: Int)Int
  
  implicit val z: Int = 4                         //> z  : Int = 4
  
  val r = add("1","2")                            //> r  : Int = 7
  
}