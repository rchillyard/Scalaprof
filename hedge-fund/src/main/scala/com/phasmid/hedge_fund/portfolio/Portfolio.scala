package com.phasmid.hedge_fund.portfolio

/**
 * CONSIDER moving this into model package
 * 
 * @author robinhillyard
 */
case class Portfolio(name: String, positions: Seq[Position])

case class Position(symbol: String, quantity: Int, contracts: Seq[Contract])

case class Contract(id: String)

object PortfolioParser {
  import spray.json.DefaultJsonProtocol
  import spray.json._

  object MyJsonProtocol extends DefaultJsonProtocol with NullOptions {
    implicit val contractFormat = jsonFormat1(Contract)
    implicit val positionFormat = jsonFormat3(Position)
    implicit val portfolioFormat = jsonFormat2(Portfolio)
  }

  import MyJsonProtocol._

  def decode(json: String): Portfolio =
    json.parseJson.convertTo[Portfolio]
}
