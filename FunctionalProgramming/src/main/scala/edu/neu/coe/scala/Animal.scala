package edu.neu.coe.scala
package animal

trait Animal
trait Dog extends Animal {
  def name: String
}
case class CairnTerrier(name: String, var stripped: Boolean) extends Dog
trait Grooming[A <: Dog, B >: Dog] extends Function1[A, B]
// see https://en.wikipedia.org/wiki/Cairn_Terrier#Grooming
class Stripping extends Grooming[CairnTerrier,Animal] {
  def apply(x: CairnTerrier) = {x.stripped = true; x}
}

trait MyIterable[T] extends Iterable[T]
object CairnTerrier {
  def apply(name: String): CairnTerrier = new CairnTerrier(name,false)
}
