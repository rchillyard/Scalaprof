package edu.neu.coe.scala

import org.scalatest.{ FlatSpec, Matchers }
import org.scalatest.concurrent._
import java.net.URL
import scala.util._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import org.scalatest._
import org.scalatest.matchers.ShouldMatchers._

/**
 * @author scalaprof
 */
class MonadOpsSpec extends FlatSpec with Matchers with Futures with ScalaFutures {

  "lift(Future[Try[T]])" should "succeed for http://www.google.com" in {
    val x = Future(Try(new URL("http://www.google.com")))
    val f = MonadOps.flatten(x)
    whenReady(f) { s => s should matchPattern { case x: URL => } }
  }
  
  "lift(Try[Future[T]])" should "succeed for http://www.google.com" in {
    val x = Try(Future(new URL("http://www.google.com")))
    val f = MonadOps.flatten(x)
    whenReady(f) { s => s should matchPattern { case x: URL => } }
  }
  
  "sequence(Seq[Future[T]])" should "succeed for http://www.google.com, etc." in {
    val args = List("http://www.google.com","http://www.microsoft.com")
    val urls = for { arg <- args; url = Future(new URL(arg))} yield url
    whenReady(Future.sequence(urls)) { s => Assertions.assert(s.length==2) }
  }
  
  "sequence(Seq[Try[T]])" should "succeed for http://www.google.com, etc." in {
    val args = List("http://www.google.com","http://www.microsoft.com")
    val urls = for { arg <- args; url = Try(new URL(arg))} yield url
    MonadOps.sequence(urls) match {
      case Success(s) => Assertions.assert(s.length==2)
      case _ => Failed
    }
  }
  
  it should "succeed for empty list" in {
    val urls = for { arg <- List[String](); url = Try(new URL(arg))} yield url
    MonadOps.sequence(urls) match {
      case Success(s) => Assertions.assert(s.length==0)
      case _ => Failed
    }
  }
  
  it should "fail for www.google.com, etc." in {
    val args = List("www.google.com","http://www.microsoft.com")
    val urls = for { arg <- args; url = Try(new URL(arg))} yield url
    MonadOps.sequence(urls) match {
      case Failure(e) => Succeeded
      case _ => Failed
    }
  }
  
  "flatten" should "succeed for http://www.google.com, etc." in {
    val args = List("http://www.google.com","http://www.microsoft.com")
    val urls = for { arg <- args; url = Future(new URL(arg))} yield url
    val fs = List(Future.sequence(urls))
    whenReady(MonadOps.flatten(fs)) { s => Assertions.assert(s.length==2) }
  }
  
  it should "succeed for empty list" in {
    val args = List[String]()
    val urls = for { arg <- args; url = Future(new URL(arg))} yield url
    val fs = List(Future.sequence(urls))
    whenReady(MonadOps.flatten(fs)) { s => Assertions.assert(s.length==0) }
  }
  
  "sequence" should "succeed for http://www.google.com, www.microsoft.com" in {
    val gs = Seq("http://www.google.com","http://www.microsoft.com","www.microsoft.com")
    val ufs = for { g <- gs; uf = Future(new URL(g))} yield uf
    val efs = for {uf <- ufs} yield MonadOps.sequence(uf)
    val esf = Future.sequence(efs)
    whenReady(esf) { es => Assertions.assert(es.length==3) }
    whenReady(esf) { es => (es(0),es(1)) should matchPattern { case (Right(x),Right(y)) => } }
    whenReady(esf) { es => es(2) should matchPattern { case Left(x) => } }
  }

  "sequence(Future=>Future(Either))" should "succeed for http://www.google.com, www.microsoft.com" in {
    val gs = Seq("http://www.google.com","http://www.microsoft.com","www.microsoft.com")
    val efs = for { g <- gs; uf = Future(new URL(g))} yield MonadOps.sequence(uf)
    for { ef <- efs } whenReady(ef) {e => e match { case Right(e) => true; case Left(e) => true; case _ => Assertions.fail()}}
  }
}