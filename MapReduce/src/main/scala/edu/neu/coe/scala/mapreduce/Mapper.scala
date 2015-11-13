package edu.neu.coe.scala.mapreduce

import akka.actor.{ Actor, ActorLogging, ActorRef }

/**
 * Note that this actor has no state. This is perhaps an anti-pattern 
 * @author scalaprof
 */
class Mapper[X,K,V](f: (X)=>Map[K,V]) extends Actor with ActorLogging {
  
  override def receive = {
    case xs: Seq[X] =>
      log.info(s"received Seq[X]: with ${xs.length} elements")
      val r = for {x <- xs} yield f(x)
      sender ! Shuffle(r)
    case x: Any =>
      log.warning(s"received unknown message type: {}",x)
  }
}