package edu.neu.coe.csye._7200
package animal

trait Animal {
  def alive: Boolean
}
trait Dog extends Animal {
  def name: String
  def alive = true
}
case class CairnTerrier(name: String, var stripped: Boolean = false) extends Dog
case class Chuweenie(name: String) extends Dog
trait Grooming[A <: Dog, B >: Dog] extends (A=>B)
// see https://en.wikipedia.org/wiki/Cairn_Terrier#Grooming
class Stripping extends Grooming[CairnTerrier,Animal] {
  def apply(x: CairnTerrier) = {x.stripped = true; x.asInstanceOf[Animal]}
}

object CairnTerrier extends App {
  def apply(name: String): CairnTerrier = new CairnTerrier(name,false)
  
  val cindy = CairnTerrier("Cindy")
  val grooming = new Stripping()
  grooming(cindy).alive
  val bentley = Chuweenie("Bentley")
  // grooming(bentley) does not compile because Bentley is not a CairnTerrier
  // grooming(cindy).name does not compile because Animals don't have names
}
