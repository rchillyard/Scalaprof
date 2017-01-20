package com.phasmid.hedge_fund.model

/**
 * @author robinhillyard
 */
object MapUtils {
  import scala.language.postfixOps
  def flatten[K, V](voKm: Map[K, Option[V]]): Map[K, V] =
    (for ((k,vo) <- voKm.toSeq) yield for (v <- vo) yield k->v).flatten.toMap 
}