package edu.neu.coe.scala.mapreduce

import akka.actor.{ Actor, ActorLogging, ActorRef }
import scala.collection.mutable.HashMap

//
/**
 * @author scalaprof
 */
class Reducer[K,V](f: (V,V)=>V, zero: () => V) extends Actor with ActorLogging {
  
  val keyMap = HashMap[K,V]()
  
  override def receive = {
    case r: Reduction[K,V] =>
      log.info(s"received Reduction with ${r.counts.length} elements")
      processReduction(r.counts)
      sender ! Result(keyMap.toMap)
    case x: Any =>
      log.warning(s"received unknown message type: {}",x)
  }
  
  def processReduction(counts: Seq[(K,V)]) {
    for (
        (k,v) <- counts
        ) updateKeyMap(k,v)
  }
  
  def updateKeyMap(k: K, v: V) {
    val s = try {
        keyMap(k)
      }
    catch {
      case e: NoSuchElementException => {
        zero()
      }
    }
   keyMap.put(k, f(s,v))
  }
}