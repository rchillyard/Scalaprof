package edu.neu.coe.scala

trait Document[K, +V] extends (Seq[K]=>V) {
  def get(x: Seq[K]): Option[V]
  def get(x: String)(implicit conv: String=>K): Option[V] = ???
}
case class Leaf[V](value: V) extends Document[Any,V] {
  def get(x: Seq[Any]): Option[V] = x match {
    case Nil => Some(value)
    case _ => None
  }
}
case class Clade[K,V](branches: Map[K,Document[K,V]]) extends Document[K,V] {
  def get(x: Seq[K]): Option[V] = x match {
    case h::t => branches.get(h) flatMap {_.get(t)}
    case Nil => None
  }
}
object Document {
  def apply[V](v: V): Document[Any,V] = Leaf(v)
  def apply[K,V](map: Map[K,V]): Document[K,V] = Clade(for ((k,v) <- map) yield (k->apply(v)))
}
