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
 * TODO this class still needs work.
 * In particular, the doMap and doReduce methods should return Future objects (without any waiting)
 * and the method waitFor should then be able to remove the dependency on Await.
 * At most there should be one Await in an entire program.
 */
class Master[X, K, V](fM: (X)=>Map[K,V], fR: (V,V)=>V, fT: (V,V)=>V, zeroV: () => V) extends Actor with ActorLogging {
  val mappers = for (i <- 1 to 3) yield context.actorOf(Props.create(classOf[Mapper[X,K,V]], fM), s"mapper-$i")
  val reducers = for (i <- 1 to 3) yield context.actorOf(Props.create(classOf[Reducer[K,V]], fR, zeroV), s"reducer-$i")
  val shuffleMap = MutableList[(K,V)]()
  val mapperMap = HashMap[Int,MutableList[X]]()
  val reducerMap = HashMap[Int,MutableList[(K,V)]]()
  var total: V = zeroV()
  implicit val timeout = Timeout(5 seconds)
  import context.dispatcher
    
  override def receive = {
    case xs: Seq[X] =>
      log.info(s"received Seq[X]: with ${xs.length} elements")
      doMap(xs)
      doReduce()
      sender ! Finish(total)
    case x: Any =>
      log.warning(s"received unknown message type: {}",x)
  }
  
  def doMap(xs: Seq[X]) {
    for (x <- xs) doMap(x)
    val r = for (i <- mapperMap.keySet) yield doMap(mappers(i), mapperMap(i))
    val f = Future.sequence(r.toSeq)
    f.onComplete {
      case Success(is) => for (i <- is) processShuffle(i.maps)
      case Failure(x) => log.warning(x.getLocalizedMessage)
    }
    waitFor(f)
    log.info(s"map stage complete: shuffle map has ${shuffleMap.size} entries")
  }
  
  def doReduce() {
    for ((k,v) <- shuffleMap) doReduce(k,v)
//    log.info(s"send messages to reducers based on reducerMap: $reducerMap")
    val fs = for (k <- reducerMap.keySet.toSeq) yield doReduce(reducers(k), reducerMap(k))
    val f = Future.sequence(fs)
    f.onComplete {
      case Success(rs) => for (r <- rs) processResult(r.map)
      case Failure(x) => log.warning(x.getLocalizedMessage)
    }
    waitFor(f)
    log.info("reduce stage complete")
  }

  def doMap(mapper: ActorRef, xs: Seq[X]): Future[Shuffle[K,V]] = (mapper ? xs).mapTo[Shuffle[K,V]]
  
  def doMap(x: X) {
    val hash = math.abs(x.hashCode)
    val which = hash % mappers.size
    val s = mapperMap.get(which) getOrElse(MutableList[X]())
    s += x
    mapperMap.put(which,s)
  }
  
  def processShuffle(ms: Seq[Map[K,V]]) {
    for (m <- ms; (k,v) <- m) shuffleMap += ((k,v))
  }
  
  def doReduce(reducer: ActorRef, ts: Seq[(K,V)]): Future[Result[K,V]] = (reducer ? Reduction(ts)).mapTo[Result[K,V]]
  
  def doReduce(k: K, v: V) {
    val hash = math.abs(k.hashCode)
    val which = hash % reducers.size
    val x = reducerMap.get(which) getOrElse(MutableList[(K,V)]())
    x += k -> v
    reducerMap.put(which,x)
  }
  
  def processResult(map: Map[K,V]) {
    for (k <- map.keySet)
      total = fT(total,map(k))
  }
  
  def waitFor[X](f: Future[X]) = {
    // This is somewhat bizarre!
    // Why should I have to wait until f is complete when that's what Await is supposed to do!
    while(!f.isCompleted) Thread.sleep(100)
    Await.ready(f,10.second)
  }
}

case class Reduction[K,V](counts: Seq[(K,V)])

case class Shuffle[K, V](maps: Seq[Map[K,V]])

case class Result[K,Y](map: Map[K,Y])

object StartReduce

case class Finish[V](total: V)