package edu.neu.coe.scala

import org.scalatest.{ FlatSpec, Matchers }

/**
 * @author scalaprof
 */
class DocumentSpec extends FlatSpec with Matchers {
//  "Document(1)" should "be Leaf(1)" in {
//    Document(1) should matchPattern { case Leaf(1) => }
//  }
//  "Document(Map)" should "be Clade(...)" in {
//    val doc = Document(Map(1->"A",2->"B"))
//    doc should matchPattern { case Clade(m) => }
//    doc.asInstanceOf[Clade[Int,String]].branches shouldBe Map(1->Document("A"),2->Document("B"))
//  }
//  "Document(1).get" should "yield Some(1) for Nil, None for i" in {
//    val doc = Document(1)
//    doc.get(Nil) should matchPattern { case Some(1) => }
//    doc.get(Seq("i")) should matchPattern { case None => }
//  }
//  "Clade.get" should "work appropriately for one level" in {
//    val one = Document(1)
//    val doc = Clade(Map("one" -> one))
//    doc.get(Nil) should matchPattern { case None => }
//    doc.get(Seq("one")) should matchPattern { case Some(1) => }
//  }
//  it should "work appropriately for two levels" in {
//    val one = Document(1)
//    val doc1 = Clade(Map("one" -> one))
//    val doc2 = Clade(Map("a" -> doc1))
//    doc2.get(Nil) should matchPattern { case None => }
//    doc2.get(Seq("a","one")) should matchPattern { case Some(1) => }
//  }
//  "Document(1).apply" should "yield 1 for Nil, throw exception for i" in {
//    val doc = Document(1)
//    doc(Nil) should matchPattern { case 1 => }
//    an [NoSuchElementException] should be thrownBy doc(Seq("i"))
//  }
//  "Clade.apply" should "work appropriately for one level" in {
//    val doc = Document(Map("one" -> 1))
//    an [NoSuchElementException] should be thrownBy doc(Nil)
//    doc(Seq("one")) should matchPattern { case 1 => }
//  }
//  it should "work appropriately for two levels" in {
//    val doc1 = Document(Map("one" -> 1))
//    val doc2 = Clade(Map("a" -> doc1))
//    an [NoSuchElementException] should be thrownBy doc2(Nil)
//    doc2(Seq("a","one")) should matchPattern { case 1 => }
//  }
//  "Clade.get(String)" should "yield Some(1) for a.one" in {
//    val doc1 = Document(Map("one" -> 1))
//    val doc2 = Clade(Map("a" -> doc1))
//    doc2.get("a.one") should matchPattern { case Some(1) => }
//  }
//  "Clade.get(String)" should "yield Some(1) for 1.2" in {
//    val one = Document(1)
//    val doc1 = Document(Map(2 -> 1))
//    val doc2 = Clade(Map(1 -> doc1))
//    def toInt(x : String): Int = x.toInt
//    implicit val conv = toInt _
//    doc2.get("1.2") should matchPattern { case Some(1) => }
//  }
}