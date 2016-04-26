package edu.neu.coe.scala.enums.enumeration

/**
 * @author scalaprof
 */

import org.scalatest._
import Rank._
import Suit._

class CardsSpec_Enumeration extends FlatSpec with Matchers with Inside {
 "ranks" should "be ordered properly" in {    
   assert(Ace > King)
   val ace = Suit.withName("Ace")
   assert(ace == Ace)
   val rankList = List(Ace,Trey,Four,Queen, Knave, Nine, Seven, Six,Deuce,Five,King,Ten,Eight)
   rankList.sorted.reverse  shouldBe List(Ace,King,Queen,Knave,Ten,Nine,Eight,Seven,Six,Five,Four,Trey,Deuce)
 }
 
 it should "distinguish honors" in {    
   assert(Ace.isHonor)
   assert(Deuce.isSpot)
 }
 
 "suits" should "be ordered properly" in {    
   assert(Spades > Clubs)
  val suitList = List(Clubs,Hearts,Spades,Diamonds)
  suitList.sorted.reverse shouldBe List(Spades,Hearts,Diamonds,Clubs)
 }

  it should "know the color" in {    
   assert(Hearts.isRed)
   assert(Spades.isBlack)
 }
 

}
  