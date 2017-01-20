package com.phasmid.hedge_fund.rules

import scala.collection.MapLike
import scala.collection.GenMapLike

/**
 * @author robinhillyard
 */
trait Candidate extends Function1[String, Option[Any]] {
  def identifier: String
  def ++(m: Map[String, Any]): Candidate
}
case class MapCandidate(id: String, map: Map[String, Any]) extends Candidate {
  def identifier = id
  def ++(m: Map[String, Any]) = MapCandidate(id, map ++ m)
  def apply(s: String) = map.get(s)
}