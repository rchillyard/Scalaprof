package edu.neu.coe.scala.mapreduce

import scala.collection.mutable.{HashMap,MutableList}
import scala.concurrent.{Future,Await}
import scala.concurrent.duration._
import scala.util._
import akka.actor.{ Actor, ActorSystem, Props, ActorRef, ActorLogging }
import akka.pattern.ask
import akka.util.Timeout
import java.net.URL

/**
 * @author scalaprof
 *
 * @param <X> input type: the message which this actor responds to is of type Seq[X].
 * @param <K> key type: mapper groups things by this key and reducer processes said groups.
 * @param <V> output type: the message which is sent on completion to the sender is of type Finish[V].
 * 
 * @param fMap the mapper function which takes a sequence of X values and creates a key-value map such that each reducer will process all the values with a given key
 * @param fReduce the reducer function which combines two values into one
 * @param fTotal the totaling function which, like fReduce, combines two values into one (CONSIDER eliminating this parameter and using fReduce for both situations)
 * @param fZero the function which creates a "zero" (unit) value of V
 */
class Master[X, K, V](fMap: (X)=>Map[K,V], fReduce: (V,V)=>V, fTotal: (V,V)=>V, fZero: () => V) extends Actor with ActorLogging {
  implicit val n = 3
  val mappers = for (i <- 1 to n) yield context.actorOf(Props.create(classOf[Mapper[X,K,V]], fMap), s"mapper-$i")
  val reducers = for (i <- 1 to n) yield context.actorOf(Props.create(classOf[Reducer[K,V]], fReduce, fZero), s"reducer-$i")
  implicit val timeout = Timeout(5 seconds)
  import context.dispatcher
    
  override def receive = {
    case xs: Seq[X] =>
      log.info(s"received Seq[X]: with ${xs.length} elements")
      val caller = sender
      mapReduce(xs).onComplete {
        case Success(v) => caller ! Finish(v)
        case f @ Failure(_) => log.error(s"map reduce failure: $f")
      }
    case x: Any =>
      log.warning(s"received unknown message type: $x")
  }
  
  private def mapReduce(xs: Seq[X]) = doMap(xs) flatMap { case st => doReduce(st)}
  
  private def doMap(xs: Seq[X]): Future[Seq[(K,V)]] = {
    val m = HashMap[Int,MutableList[X]]()
    
    def doMap(mapper: ActorRef, xs: Seq[X]): Future[Shuffle[K,V]] = (mapper ? xs).mapTo[Shuffle[K,V]]
  
    def updateMapperMap(x: X) {
      val i = hashIt(x,mappers.size)
      val xs = m.get(i) getOrElse(MutableList[X]())
      xs += x
      m.put(i,xs)
    }
  
    def processShuffle(zs: Seq[Shuffle[K,V]]): Seq[(K,V)] = {
      val kVs = MutableList[(K,V)]()
      for (z <- zs) for (m <- z.maps; (k,v) <- m) kVs += ((k,v))
      log.info(s"map stage complete: shuffle map has ${kVs.size} entries")
      kVs.seq
    }
    for (x <- xs) updateMapperMap(x)
    val zfs = for (i <- m.keySet) yield (mappers(i) ? m(i)).mapTo[Shuffle[K,V]]
    Future.sequence(zfs.toSeq) map {processShuffle _}
  }

  /**
   * @return an non-negative integer less than n and depending only on the hash code of x
   */
  private def hashIt(x: Any, n: Int) = math.abs(x.hashCode) % n
  
  private def doReduce(kVs: Seq[(K,V)]): Future[V] = {
    val m = HashMap[Int,MutableList[(K,V)]]()
    
    def doReduce(reducer: ActorRef, kVs: Seq[(K,V)]): Future[Result[K,V]] = (reducer ? Reduction(kVs)).mapTo[Result[K,V]]
  
    def updateReducerMap(k: K, v: V) {
      val i = hashIt(k,reducers.size)
      val kVs = m.get(i) getOrElse(MutableList[(K,V)]())
      kVs += k -> v
      m.put(i,kVs)
    }
  
    for ((k,v) <- kVs) updateReducerMap(k,v)
    val rfs = for (k <- m.keySet.toSeq) yield doReduce(reducers(k), m(k))
    val vsf = for (rs <- Future.sequence(rfs)) yield for ( r <- rs; v <- r.map.values) yield v
    for (vs <- vsf) yield vs.foldLeft(fZero()){fTotal(_,_)}
  }
}

case class Reduction[K,V](counts: Seq[(K,V)])

case class Shuffle[K, V](maps: Seq[Map[K,V]])

case class Result[K,V](map: Map[K,V])

object StartReduce

case class Finish[V](total: V)