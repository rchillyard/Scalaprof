package com.phasmid.hedge_fund.actors

import akka.actor.{ Actor, ActorLogging, ActorRef }

/**
 * @author robinhillyard
 */
abstract class BlackboardActor(blackboard: ActorRef) extends Actor with ActorLogging {
  override def receive = {
    case m => log.warning("uncaught message type: {}", m)
  }
  //  def props[A <: BlackboardActor](clazz: Class[A]) = Props.create(clazz, blackboard)
}