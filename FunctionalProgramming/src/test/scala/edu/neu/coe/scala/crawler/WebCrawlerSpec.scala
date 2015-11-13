package edu.neu.coe.scala.crawler

import org.scalatest.{ FlatSpec, Matchers }
import org.scalatest.concurrent._
import java.net.URL
import scala.io.Source
import scala.util._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import org.scalatest._
import matchers.ShouldMatchers._
import java.net.MalformedURLException
import edu.neu.coe.scala.MonadOps
import org.scalatest.time._

/**
 * @author scalaprof
 */
class WebCrawlerSpec extends FlatSpec with Matchers with Futures with ScalaFutures with Inside {

  "wget(URL)" should "succeed for http://www.htmldog.com/examples/" in {
    val t = for { url <- Try(new URL("http://www.htmldog.com/examples/"))} yield WebCrawler.wget(url)
    whenReady(MonadOps.flatten(t), timeout(Span(6, Seconds))) { s => Assertions.assert(s.length>100) }
  }

  "wget(Seq[URL])" should "succeed for http://www.htmldog.com/examples/, http://www.google.com" in {
    val args = List("http://www.htmldog.com/examples/","http://www.google.com")
    val tries = for ( arg <- args toList ) yield Try(new URL(arg))
    val y = for { us <- MonadOps.sequence(tries) } yield WebCrawler.wget(us)
    whenReady(MonadOps.flatten(y), timeout(Span(6, Seconds))) { s => Assertions.assert(s.length>100) }
  }

  "crawler(Seq[URL])" should "succeed for test.html, depth 1" in {
    val project = "/Users/scalaprof/ScalaClass/FunctionalProgramming"
    val dir = "src/test/scala"
    val pkg = "edu/neu/coe/scala/crawler"
    val file = "test.html"
    val args = List(s"file://$project/$dir/$pkg/$file")
    val tries = for ( arg <- args toList ) yield Try(new URL(arg))
//    println(s"tries: $tries")
    val usft = for { us <- MonadOps.sequence(tries) } yield WebCrawler.crawler(us)
    whenReady(MonadOps.flatten(usft), timeout(Span(20, Seconds))) { s => Assertions.assert(s.length==2) }
  }
//  it should "succeed for test.html, depth 2" in {
//    val project = "/Users/scalaprof/ScalaClass/FunctionalProgramming"
//    val dir = "src/test/scala"
//    val pkg = "edu/neu/coe/scala/crawler"
//    val file = "test.html"
//    val args = List(s"file://$project/$dir/$pkg/$file")
//    val tries = for ( arg <- args toList ) yield Try(new URL(arg))
//    println(s"tries: $tries")
//    val usft = for { us <- MonadOps.sequence(tries) } yield WebCrawler.crawler(us,3,List())
//    whenReady(MonadOps.lift(usft), timeout(Span(20, Seconds))) { s => Assertions.assert(s.length>100) }
//  }


}