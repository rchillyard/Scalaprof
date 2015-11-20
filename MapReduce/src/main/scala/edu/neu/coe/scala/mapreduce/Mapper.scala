package edu.neu.coe.scala.mapreduce

import akka.actor.{ Actor, ActorLogging, ActorRef }

/**
 * Note that this actor has no state. This is perhaps an anti-pattern 
 * @author scalaprof
 *
 * @param <X> input type: the message which this mapper responds to is of type Seq[X]
 * @param <K> output key type: the response to the input message is of the form Shuffle[K,V]
 * @param <V> output value type: the response to the input message is of the form Shuffle[K,V]
 * 
 * Note that the Shuffle message type is of type Seq[Map[K,V]]
 */
class Mapper[X,K,V](f: (X)=>Map[K,V]) extends Actor with ActorLogging {
  
  override def receive = {
    case xs: Seq[X] =>
      log.info(s"received Seq[X]: with ${xs.length} elements")
      sender ! Shuffle(xs map f)
    case x: Any =>
      log.warning(s"received unknown message type: {}",x)
  }
}