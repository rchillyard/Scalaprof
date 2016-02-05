package edu.neu.coe.scala.crawler

import java.net.URL
import scala.io.Source
import scala.util._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.xml.Node
import edu.neu.coe.scala.MonadOps

/**
 * @author scalaprof
 */
object WebCrawler extends App { 

  def getURLContent(u: URL): Future[String] = {
    for {
      source <- Future(Source.fromURL(u))
    } yield source mkString
  }
  
  def wget(u: URL): Future[Seq[URL]] = {
  // TODO implement. 15 points. Hint: write as a for-comprehension, using the constructor new URL(URL,String) to get the appropriate URL for relative links 
    def getURLs(ns: Node): Seq[URL] = ???
    def getLinks(g: String): Try[Seq[URL]] = 
      for (n <- HTMLParser.parse(g) recoverWith({case f=>Failure(new RuntimeException(s"parse problem with URL $u: $f"))}))
        yield getURLs(n)
	// TODO implement. 8 points. Hint: write as a for-comprehension, using getURLContent (above) and getLinks above. You might also need MonadOps.future
 	??? 
  }

  def wget(us: Seq[URL]): Future[Seq[Either[Throwable,Seq[URL]]]] = {
    val us2 = us.distinct take 10
    // TODO implement the rest of this, based on us2 instead of us. 12 points.
    // Hint: Use wget(URL) (above). MonadOps.sequence and Future.sequence are also available to you to use.
    ???
  }
    
  def crawler(depth: Int, args: Seq[URL]): Future[Seq[URL]] = {
		def inner(urls: Seq[URL], depth: Int, accum: Seq[URL]): Future[Seq[URL]] =
		  if ( depth>0 )
			  for (us <- MonadOps.flattenRecover(wget(urls),{x => System.err.println(x)}); r <- inner(us,depth-1,accum ++: urls)) yield r
			else
			  Future.successful(accum)
    inner(args, depth, List())
  }
}
