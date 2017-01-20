package com.phasmid.hedge_fund.actors

import akka.actor.{ ActorRef, Props }
import spray.http._
import akka.actor.Identify
import akka.actor.ActorIdentity

/**
 * @author robinhillyard
 */
class MarketData(blackboard: ActorRef) extends BlackboardActor(blackboard) {

  /**
   * see definition of get(String)
   */
  val instruments = scala.collection.mutable.Map[String, Map[String, String]]()

  override def receive = {
    case KnowledgeUpdate(model, identifier, update) =>
      if (log.isDebugEnabled ) log.debug("update to identifier: {}: {}", identifier, update)
      else log.info(s"update to identifier: $identifier")
      instruments.put(identifier, update)
      // for a stock, we don't need additional attributes
      blackboard ! Confirmation(identifier, model, Map())

    // CONSIDER allowing key to be null in which case all attributes returned
    // Or allow key to be a list and always return a map of values
    case SymbolQuery(identifier, keys) =>
      log.debug("symbol query received re: identifier: {} and key {}", identifier, keys)
      val attributes: List[Option[(String, String)]] = instruments.get(identifier) match {
        case Some(a) => keys map { k =>
          a.get(k) match {
            case Some(v) => Some(k -> v)
            case None => None
          }
        }
        case None => List()
      }
      val y = (attributes flatten).toMap;
      log.debug(s"creating QueryResponse: $identifier $y")
      sender ! QueryResponseValid(identifier, y)

    case OptionQuery(key, value) =>
      log.info("option query received re: key: {} and value {}", key, value)
      val optInstr = instruments find { case (k, v) => v.get(key) match { case Some(`value`) => true; case _ => false } }
      optInstr match {
        case Some((x, m)) => sender ! QueryResponseValid(x, m)
        case _ => log.warning("no match found for key: {}, value: {}", key, value); sender ! QueryResponseNone
      }

    case m => super.receive(m)
  }

  /**
   * The key to the instruments collection is an "identifier".
   * In the case of stocks and similar instruments with a (ticker) symbol (or CUSIP),
   * then identifier is the symbol.
   * In the case of options, the identifier is option id.
   * @param key
   * @return
   */
  private def get(key: String) = instruments.get(key)
}

