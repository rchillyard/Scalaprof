package edu.neu.coe.scala.list

import org.scalatest.{ FlatSpec, Matchers }
import edu.neu.coe.scala.list._

/**
 * @author scalaprof
 */
class ListSpec extends FlatSpec with Matchers {

  "Nil" should "have zero length" in {
    Nil.length should be (0)
  }
  it should "equal Nil" in {
    Nil.equals(Nil) shouldBe true
  }
  it should "be empty" in {
    Nil.isEmpty shouldBe true
  }
  it should "have Nil tail" in {
    Nil.x3 shouldBe Nil
  }
  it should "should throw an exception on get" in {
    val x: List[Nothing] = Nil
    an [IndexOutOfBoundsException] should be thrownBy x.apply(0)
  }
  it should "leave any list unchanged on prepend" in {
    val x = List(1,2,3) // arbitrary
    Cons(Nil,x).equals(x) shouldBe true
  }
  it should "leave any list unchanged on append" in {
    val x = List(1,2,3) // arbitrary
    (x++Nil).equals(x) shouldBe true
  }
  
  "List(1,2,3)" should "have 3 length" in {
    List(1,2,3).length should be (3)
  }
  it should "equal List(1,2,3)" in {
    List(1,2,3).equals(List(1,2,3)) shouldBe true
  }
  it should "remain unchanged on addition of Nil" in {
    val x = List(1,2,3)
    Cons(Nil,x).equals(x) shouldBe true
  }
  it should "not be empty" in {
    List(1,2,3).isEmpty shouldBe false
  }
  it should "have Nil tail" in {
    List(1,2,3).x3 should be (List(2,3))
  }
  it should "be 3 on x4(2)" in {
    val x: List[Int] = List(1,2,3)
    x.apply(2) should be (3)
  }
  it should "be List(1,2,3) on map" in {
    val x: List[Int] = List(1,2,3)
    x.map({_.toString}) shouldBe List("1","2","3")
  }
  it should "be List(1,2,3) on flatMap" in {
    val x: List[Int] = List(1,2,3)
    x.flatMap({e => List(e.toString)}) shouldBe List("1","2","3")
  }
  it should "be List(1,2,3,4,5,6) on ++" in {
    val x: List[Int] = List(1,2,3)
    val y: List[Int] = List(4,5,6)
    (x++y) shouldBe List(1,2,3,4,5,6)
  }
  it should "have length 2 on ++" in {
    val x: List[CharSequence] = List(new StringBuffer("A"))
    val y: List[String] = List("B")
    (x++y).length should be (2)
  }
  it should "have length 2 on ++ (2)" in {
    val x: List[CharSequence] = List(new StringBuffer("A"))
    val y: List[String] = List("B")
    (y++x).length should be (2)
  }
}