package com.phasmid.hedge_fund.model

/**
 * @author robinhillyard
 */
trait Model {
  def getKey(query: String): Option[String]
  def isOption: Boolean
  // CONSIDER dealing with any missing keys as error condition
  def mapKeys(list: List[String]): List[String] = list flatMap { k => getKey(k) }
  override def toString() = getKey("name") match { case Some(x) => "model: " + x; case _ => "unnamed model" }
}