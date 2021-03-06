package com.phasmid.hedge_fund.model

import spray.http.Uri._
import spray.http.Uri
import com.phasmid.hedge_fund.http.UriGet

/**
 * @author robinhillyard
 */

case class GoogleQuery(exchange: String) extends Query {
  val uriGet = new UriGet()

  def createQuery(symbols: List[String]): Uri = {
    //https://www.google.com/finance/option_chain?q=NASDAQ%3AMSFT&ei=qw-xVbnzC9DDeJWDLQ
    val symbolList = symbols mkString ","
    val exchangeName = if (exchange != null) s"$exchange:" else ""
    val queryParams = Map("q" -> s"${exchangeName}${symbolList}", "client" -> "ig")
    uriGet.get(GoogleQuery.server, GoogleQuery.path, queryParams)
  }

  def getProtocol = "json:GF"
}

object GoogleQuery {
  val server = "finance.google.com"
  val path = "/finance/info"
}

class GoogleModel extends Model {
  def isOption = false
  def getKey(query: String) = query match {
    case "name" => Some("GF")
    case "symbol" => Some("t")
    case "price" => Some("l")
    case _ => None
  }
}
