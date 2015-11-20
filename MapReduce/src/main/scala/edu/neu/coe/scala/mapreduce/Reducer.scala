package edu.neu.coe.scala.mapreduce

import akka.actor.{ Actor, ActorLogging, ActorRef }
import scala.collection.mutable.HashMap

/**
 * @author scalaprof
 *
 * @param <K> key type: the input message is of type Reduction[K,V], i.e. Seq[(,K,V)] and
 * the response is of type Result[K,V], i.e. Map[K,V]
 * @param <V> value type. See key type for more detail.
 */
class Reducer[K,V](fReduce: (V,V)=>V, zero: () => V) extends Actor with ActorLogging {
  
  val keyMap = HashMap[K,V]()
  
  override def receive = {
    case r: Reduction[K,V] =>
      log.info(s"received Reduction with ${r.counts.length} elements")
      processReduction(r.counts)
      sender ! Result(keyMap.toMap)
    case x: Any =>
      log.warning(s"received unknown message type: {}",x)
  }
  
  private def processReduction(counts: Seq[(K,V)]) {
    for ( (k,v) <- counts ) updateKeyMap(k,v)
  }
  
  private def updateKeyMap(k: K, v: V) {
    val w = try { keyMap(k) }
    catch {
      case e: NoSuchElementException => {
        zero()
      }
    }
   keyMap.put(k, fReduce(w,v))
  }
}