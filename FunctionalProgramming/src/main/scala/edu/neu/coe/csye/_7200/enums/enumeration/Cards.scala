package edu.neu.coe.csye._7200.enums.enumeration

/**
 * @author scalaprof
 */

object Rank extends Enumeration {
   type Rank = Value
   val Deuce, Trey, Four, Five, Six, Seven, Eight, Nine, Ten, Knave, Queen, King, Ace = Value
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
   val Clubs, Diamonds, Hearts, Spades = Value
   class SuitValue(suit: Value) {
      def isRed = !isBlack
      def isBlack = suit match {
         case Clubs | Spades => true
         case _              => false
      }
   }
   implicit def value2SuitValue(suit: Value) = new SuitValue(suit)
} 

import edu.neu.coe.csye._7200.enums.enumeration.Rank._
import edu.neu.coe.csye._7200.enums.enumeration.Suit._

case class Card (rank: Rank, suit: Suit)
