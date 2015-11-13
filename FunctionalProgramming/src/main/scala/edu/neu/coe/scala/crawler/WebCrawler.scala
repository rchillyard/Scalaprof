package edu.neu.coe.scala.crawler

import scala.concurrent.Future
import java.net.URL
import scala.io.Source
import scala.util._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author scalaprof
 */
object WebCrawler extends App { 

  def getURLContent(url: URL): Future[String] = {
    for {
      connection <- Future(url.openConnection())
      is <- Future(connection.getInputStream)
      source = Source.fromInputStream(is)
    } yield source.mkString
  }
  
  def wget(url: URL): Future[Seq[Try[URL]]] = {
    def getLinks(content: String): Seq[Try[URL]] = {
      def getURL(s: String) = Try(new URL(url,s))
      println(s"parsing $url")
      (HTMLParser.parse(content) \\ "a") map(_ \ "@href") map (_ toString) map (getURL(_))
    }
    for { content <- getURLContent(url) } yield getLinks(content)
  }

  def wget(urls: Seq[URL]): Seq[Future[Seq[Try[URL]]]] = for { url <- urls } yield wget(url)
  
  def liftAndFlatten(x: Seq[Future[Seq[Try[URL]]]]): Future[Seq[Try[URL]]] = Future.sequence(x) map {_ flatten}
  
  def crawler(args: Seq[Try[URL]], depth: Int): Unit = if (depth>0 ) {
    val q = for {arg <- args; t <- arg.toOption} yield t
    val r = liftAndFlatten(wget(q))
    r.onComplete {case Success(x) => println(s"Links: $x"); crawler(x, depth-1); case Failure(z) => println(s"failure: $z")}
  }
  
println(s"web reader: ${args.toList}")
  val urls = for ( arg <- args toList ) yield Try(new URL(arg))
  crawler(urls, 2)
  
  Thread.sleep(10000)
}
