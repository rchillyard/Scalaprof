package edu.neu.coe.scala.mapreduce

import scala.collection.mutable.{HashMap,MutableList}
import scala.concurrent.{Future,Await,Promise}
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
      mapReduce(xs,sender)
    case x: Any =>
      log.warning(s"received unknown message type: {}",x)
  }
  
  private def mapReduce(xs: Seq[X], caller: ActorRef) = {
      doMap(xs).onComplete {
        case Success(st) => doReduce(st, caller)
        case f @ Failure(_) => f
      }
  }
  
  private def doMap(xs: Seq[X]): Future[Seq[(K,V)]] = {
    val mapperMap = HashMap[Int,MutableList[X]]()
    
    def doMap(mapper: ActorRef, xs: Seq[X]): Future[Shuffle[K,V]] = (mapper ? xs).mapTo[Shuffle[K,V]]
  
    def updateMapperMap(x: X) {
      val hash = math.abs(x.hashCode)
      val which = hash % mappers.size
      val s = mapperMap.get(which) getOrElse(MutableList[X]())
      s += x
      mapperMap.put(which,s)
    }
  
    def processShuffle(is: Seq[Shuffle[K,V]]) = {
      val shuffleMap = MutableList[(K,V)]()
      for (i <- is) for (m <- i.maps; (k,v) <- m) shuffleMap += ((k,v))
      log.info(s"map stage complete: shuffle map has ${shuffleMap.size} entries")
      shuffleMap.seq
    }
    for (x <- xs) updateMapperMap(x)
    val r = for (i <- mapperMap.keySet) yield (mappers(i) ? mapperMap(i)).mapTo[Shuffle[K,V]]
    val f = Future.sequence(r.toSeq)
    val result = Promise[Seq[(K,V)]]()
    f.onComplete {
      case Success(is) => result.complete(Try(processShuffle(is)))
      case Failure(x) => result.complete(Try(throw x))
    }
    result.future
  }
  
  private def doReduce(shuffles: Seq[(K,V)], caller: ActorRef) {
    val reducerMap = HashMap[Int,MutableList[(K,V)]]()
    
    def doReduce(reducer: ActorRef, ts: Seq[(K,V)]): Future[Result[K,V]] = (reducer ? Reduction(ts)).mapTo[Result[K,V]]
  
    def updateReducerMap(k: K, v: V) {
      val hash = math.abs(k.hashCode)
      val which = hash % reducers.size
      val x = reducerMap.get(which) getOrElse(MutableList[(K,V)]())
      x += k -> v
      reducerMap.put(which,x)
    }
  
    for ((k,v) <- shuffles) updateReducerMap(k,v)
    val rfs = for (k <- reducerMap.keySet.toSeq) yield doReduce(reducers(k), reducerMap(k))
    val rsf = Future.sequence(rfs)
    val vsf = rsf map {rs => for (r <- rs; v <- r.map.values) yield v}
    val vf = vsf map {vs => vs.foldLeft(fZero()){fTotal(_,_)}}
    vf.onComplete {
      case Success(v) => log.info(s"reduce stage complete with total=$v"); caller ! Finish(v)
      case Failure(x) => log.warning(x.getLocalizedMessage)
    }
  }
}

case class Reduction[K,V](counts: Seq[(K,V)])

case class Shuffle[K, V](maps: Seq[Map[K,V]])

case class Result[K,V](map: Map[K,V])

object StartReduce

case class Finish[V](total: V)