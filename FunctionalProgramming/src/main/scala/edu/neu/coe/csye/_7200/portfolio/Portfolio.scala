package edu.neu.coe.csye._7200.portfolio

case class Portfolio(name: String, positions: Seq[Position])

case class Position(symbol: String, quantity: Double)

/**
 * @author scalaprof
 */
object Portfolio extends App {
  import spray.json._
  object PortfolioJsonProtocol extends DefaultJsonProtocol {
    implicit val positionFormat = jsonFormat2(Position)
    implicit val portfolioFormat = jsonFormat2(Portfolio.apply)
  }

  ??? // TODO 25 points. Write poets out as Json. Show the Json in the console...
      // ...Read the Json file back as poets1 and write that out as XML. Show it on console.
      // Show the comparison of the XML file you produced with the poets.xml file (as part of your submission).
}