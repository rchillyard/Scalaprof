package com.phasmid.hedge_fund

import com.phasmid.hedge_fund.model._
import com.phasmid.hedge_fund.actors._
import com.phasmid.hedge_fund.portfolio.{Portfolio,PortfolioParser}
import akka.actor.{ Actor, ActorSystem, Props, ActorRef }
import com.typesafe.config.{ ConfigFactory, Config }
import scala.io.Source
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author robinhillyard
 */
object HedgeFund extends App {

    val config = ConfigFactory.load()
    implicit val system = ActorSystem("HedgeFund")
    println(s"""${config.getString("name")}, ${config.getString("appVersion")}""")
    val engines: Seq[Query] = config.getString("engine") match {
      case "YQL" => Seq(YQLQuery(config.getString("format"), false))
      case "Google" => Seq(GoogleQuery("NASDAQ"))
      case "YQL,Google" => Seq(YQLQuery(config.getString("format"), false),GoogleQuery("NASDAQ"))
      case _ => Seq()
    }
    println(s"engines: $engines")
    val portfolio = getPortfolio(config)
    val blackboard = system.actorOf(Props.create(classOf[HedgeFundBlackboard]), "blackboard")
    val symbols = getSymbols(config,portfolio)
    for (engine <- engines) blackboard ! ExternalLookup(engine.getProtocol, engine.createQuery(symbols))
    val optionEngine = new GoogleOptionQuery
    symbols foreach {
      s => blackboard ! ExternalLookup(optionEngine.getProtocol, optionEngine.createQuery(List(s)))
    }
    blackboard ! PortfolioUpdate(portfolio)

import scala.language.postfixOps
  def getSymbols(config: Config, portfolio: Portfolio) = {
    // TODO add in the symbols from the portfolio
    config.getString("symbols") split ("\\,") toList;
  }

def getPortfolio(config: Config): Portfolio = {
   val json = Source.fromFile(config.getString("portfolio")) mkString
   val portfolio = PortfolioParser.decode(json)
   println(s"portfolio: $portfolio")
  portfolio
  }
}
