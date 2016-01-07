package edu.neu.coe.scala.enums.enumeration

/**
 * @author scalaprof
 */

object Rank extends Enumeration {
   type Rank = Value
   val Ace, King, Queen, Knave, Ten, Nine, Eight, Seven, Six, Five, Four, Trey, Deuce = Value
   class RankValue(rank: Value) {
     def isSpot = !isHonor
     def isHonor = rank match {
       case Ace | King | Queen | Knave | Ten => true
       case _ => false
     }
   }
   implicit def value2RankValue(rank: Value) = new RankValue(rank)
}

object Suit extends Enumeration {
   type Suit = Value
   val Spades, Hearts, Diamonds, Clubs = Value
   class SuitValue(suit: Value) {
      def isRed = !isBlack
      def isBlack = suit match {
         case Clubs | Spades => true
         case _              => false
      }
   }
   implicit def value2SuitValue(suit: Value) = new SuitValue(suit)
} 

import Rank._
import Suit._

case class Card (rank: Rank, suit: Suit)
