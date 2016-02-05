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
import java.io.FileNotFoundException
import scala.collection.mutable.MutableList

/**
 * @author scalaprof
 */
class WebCrawlerSpec extends FlatSpec with Matchers with Futures with ScalaFutures with TryValues with Inside {

  "wget(URL)" should "succeed for http://www.htmldog.com/examples/" in {
    val usfy = for { u <- Try(new URL("http://www.htmldog.com/examples/"))} yield WebCrawler.wget(u)
    whenReady(MonadOps.flatten(usfy), timeout(Span(6, Seconds))) { us => Assertions.assert(us.length>100) }
  }

  it should "not succeed for http://www.htmldog.com/junk/" in {
    val usfy = for { u <- Try(new URL("http://www.htmldog.com/junk/"))} yield WebCrawler.wget(u)
    val usf = MonadOps.flatten(usfy)
    whenReady(usf.failed, timeout(Span(6, Seconds))) { e => e shouldBe a [FileNotFoundException]  }
  }

  it should "not succeed for x//www.htmldog.com/examples/" in {
    val usfy = for { u <- Try(new URL("x//www.htmldog.com/examples/"))} yield WebCrawler.wget(u)
    usfy.failure.exception shouldBe a [MalformedURLException]
    usfy.failure.exception should have message "no protocol: x//www.htmldog.com/examples/"
  }

  "wget(Seq[URL])" should "succeed for http://www.htmldog.com/examples/, http://www.dataflowage.com/" in {
    val ws = List("http://www.htmldog.com/examples/","http://www.dataflowage.com/")
    val uys = for ( w <- ws ) yield Try(new URL(w))
    val usesfy = for { us <- MonadOps.sequence(uys) } yield WebCrawler.wget(us)
    val usesf = MonadOps.flatten(usesfy)
    whenReady(usesf, timeout(Span(12, Seconds))) { uses =>
      uses.size shouldBe 2
      for (use <- uses) use match {
        case Right(us) => Assertions.assert(us.length>10)
        case Left(x) => System.err.println(s"ignored error: $x")
      }
    }
  }
  
  "filterAndFlatten" should "work" in {
    val ws = List("http://www.htmldog.com/examples/")
    val uys = for ( w <- ws ) yield Try(new URL(w))
    MonadOps.sequence(uys) match {
      case Success(us1) =>
        val usefs = WebCrawler.wget(us1)
        val exceptions = MutableList[Throwable]()
        val usf = MonadOps.flattenRecover(usefs,{x => exceptions +=x})
        whenReady(usf, timeout(Span(12, Seconds))) {
          us2 => us2.distinct.size shouldBe 164
          exceptions.size shouldBe 0
        }
      case f @ _ => fail(f.toString())
    }
  }

  "crawler(Seq[URL])" should "succeed for test.html, depth 2" in {
    val project = "/Users/scalaprof/ScalaClass/FunctionalProgramming"
    val dir = "src/test/scala"
    val pkg = "edu/neu/coe/scala/crawler"
    val file = "test.html"
    val args = List(s"file://$project/$dir/$pkg/$file")
    val tries = for ( arg <- args toList ) yield Try(new URL(arg))
//    println(s"tries: $tries")
    val usft = for { us <- MonadOps.sequence(tries) } yield WebCrawler.crawler(2,us)
    whenReady(MonadOps.flatten(usft), timeout(Span(20, Seconds))) { s => Assertions.assert(s.length==2) }
  }
  ignore should "succeed for test.html, depth 3" in {
    val project = "/Users/scalaprof/ScalaClass/FunctionalProgramming"
    val dir = "src/test/scala"
    val pkg = "edu/neu/coe/scala/crawler"
    val file = "test.html"
    val args = List(s"file://$project/$dir/$pkg/$file")
    val tries = for ( arg <- args toList ) yield Try(new URL(arg))
    println(s"tries: $tries")
    val usft = for { us <- MonadOps.sequence(tries) } yield WebCrawler.crawler(3,us)
    val usf = MonadOps.flatten(usft)
    whenReady(usf, timeout(Span(60, Seconds))) { us =>
      us.size shouldBe 177
    }
  }
}