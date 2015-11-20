package edu.neu.coe.scala.mapreduce

import scala.io.Source
import scala.util._
import scala.concurrent._
import scala.concurrent.duration._
import com.typesafe.config.{ ConfigFactory, Config }
import akka.actor.{ Actor, ActorSystem, Props, ActorRef }
import akka.pattern.ask
import akka.util.Timeout
import java.net.URL

/**
 * @author scalaprof
 */
case class MapReduce[V] (master: ActorRef)(implicit system: ActorSystem) extends Function1[Array[String],Future[V]] {
  
  def apply(sa: Array[String]): Future[V] = {
    implicit val timeout: Timeout = Timeout(10 seconds)
    import system.dispatcher
    val result = Promise[V]
    val uts = for ( g <- sa ) yield Try(new URL(g))    
    val xft = for (t <- sequence(uts)) yield (master ? t).mapTo[Finish[V]]
    flatten(xft).onComplete {
      case Success(Finish(x)) => result.complete(Try(x))
      case Failure(e) => result.failure(e)
    }
    result.future
  } 
  
  def sequence[T](xs : Seq[Try[T]]) : Try[Seq[T]] = (Try(Seq[T]()) /: xs) {
    (a, b) => for (aa <- a; bb <- b ) yield aa :+ bb
  }
  
  def flatten[X](xft : Try[Future[X]]): Future[X] = xft match {
      case Success(xf) => xf
      case Failure(e) => (Promise[X] complete (throw e)).future
  }
}

object MapReduce {
  def apply[V](props: Props)(implicit system: ActorSystem): MapReduce[V] = apply(system.actorOf(props, "master"))(system)
}