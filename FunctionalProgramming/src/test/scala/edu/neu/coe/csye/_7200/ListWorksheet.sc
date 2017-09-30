package edu.neu.coe.csye._7200.list

object ListWorksheet {
  println("Welcome to the List worksheet")        //> Welcome to the List worksheet
  val x: List[CharSequence] = List(new StringBuffer("Hello"))
                                                  //> x  : edu.neu.coe.csye._7200.list.List[CharSequence] = ("Hello")
  val y: List[String] = List("Goodbye")           //> y  : edu.neu.coe.csye._7200.list.List[String] = ("Goodbye")
  x++y                                            //> res0: edu.neu.coe.csye._7200.list.List[CharSequence] = ("Hello", "Goodbye")
  y++x                                            //> res1: edu.neu.coe.csye._7200.list.List[CharSequence] = ("Goodbye", "Hello")
  
  case class MyList[+A](name: String, list: List[A]) extends List[A] {
  	override def toString: String = name+": "+list.toString()
  }
  
  val a = MyList("abc",List("A","B"))             //> a  : edu.neu.coe.csye._7200.list.ListWorksheet.MyList[String] = abc: ("A", "B")
  
  class CharCounter[A <: CharSequence] extends Counter[A] {
   def apply(s: A) = s.length
  }
  
	val charCounter = new CharCounter[CharSequence]()
                                                  //> charCounter  : edu.neu.coe.csye._7200.list.ListWorksheet.CharCounter[CharSequence
                                                  //| ] = <function1>
	charCounter.apply("hello")                //> res2: Int = 5
  
  (x++y) count charCounter                        //> res3: edu.neu.coe.csye._7200.list.List[Int] = ("5", "7")
}