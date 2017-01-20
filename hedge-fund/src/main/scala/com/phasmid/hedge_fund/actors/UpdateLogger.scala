package com.phasmid.hedge_fund.actors

import akka.actor.{ ActorRef, Props }
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.language.postfixOps
import com.phasmid.hedge_fund.model.Model
import com.phasmid.hedge_fund.portfolio._

/**
 * CONSIDER renaming this as PortfolioManager
 *
 * @author robinhillyard
 */
class UpdateLogger(blackboard: ActorRef) extends BlackboardActor(blackboard) {

  var portfolio = new Portfolio("", Nil)

  override def receive =
    {
      case Confirmation(id, model, attrs) =>
        log.debug(s"update for identifier: $id")
        if (model.isOption)
          processOption(id, model, attrs)
        else
          processStock(id, model)

      case PortfolioUpdate(p) =>
        log.debug(s"portfolio update for: ${p.name}")
        portfolio = p
        showPortfolio

      case m => super.receive(m)
    }

  implicit val timeout = Timeout(5 seconds)

  def processStock(identifier: String, model: Model) = {
    model.getKey("price") match {
      case Some(p) => {
        // sender is the MarketData actor
        val future = (sender ? SymbolQuery(identifier, List(p))).mapTo[QueryResponse]
        // TODO why are we waiting for this here?
        val result = Await.result(future, timeout.duration)
        result match {
          case QueryResponseValid(k,a) =>
            a map {
              case (k, v) => log.info(s"$identifier attribute $k has been updated to: $v")
            }
          case _ =>
        }
      }
      case None => log.warning(s"'price' not defined in model")
    }
  }

  def processOption(identifier: String, model: Model, attributes: Map[String, Any]) = {
    val key = "underlying"
    attributes.get(key) match {
      case Some(value) =>
        val future = (blackboard ? OptionQuery("id", value)).mapTo[QueryResponse]
        // TODO why are we waiting for this here?
        val result = Await.result(future, timeout.duration)
        result match {
          case QueryResponseValid(k,a) =>
              println(s"Action Required: re: qualifying option $identifier with underlying symbol: $k and attributes: $a")
          case _ =>
        }
      case None => log.warning(s"processOption: value not present for $key")
    }
  }

  def showPortfolio {
    println(s"Portfolio for ${portfolio.name}")
    portfolio.positions foreach { showPosition(_) }
  }

  def showPosition(position: Position) {
    println(s"position for ${position.symbol}: quantity=${position.quantity}; options=")
    position.contracts foreach { showContract(_) }
  }

  def showContract(contract: Contract) {
    println(s"contract: $contract")
  }
}
