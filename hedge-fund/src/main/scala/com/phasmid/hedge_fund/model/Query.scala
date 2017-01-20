package com.phasmid.hedge_fund.model

import spray.http.Uri._
import spray.http.Uri

/**
 * @author robinhillyard
 */
trait Query {
  def createQuery(symbols: List[String]): Uri
  def getProtocol: String
}