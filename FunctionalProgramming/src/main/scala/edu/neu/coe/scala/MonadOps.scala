package edu.neu.coe.scala

import scala.concurrent.{ Future, ExecutionContext, Promise }
import scala.util._

// CONSIDER re-ordering according to Assignment 5

object MonadOps {
  
  def sequence[X](xt: Try[X]): Either[Throwable,X] = ???
  
  def sequence[X](xf: Future[X])(implicit executor: ExecutionContext): Future[Either[Throwable,X]] = ???
  
  def sequence[X](xos : Seq[Option[X]]): Option[Seq[X]] = ???

  def sequence[X](xts : Seq[Try[X]]): Try[Seq[X]] = (Try(Seq[X]()) /: xts) {
    (xst, xt) => for (xs <- xst; x <- xt ) yield xs :+ x
  }

  def flatten[X](xsfs: Seq[Future[Seq[X]]])(implicit executor: ExecutionContext): Future[Seq[X]] = Future.sequence(xsfs) map {_ flatten}

  def flatten[X](xtf : Future[Try[X]])(implicit executor: ExecutionContext): Future[X] = {
		  def convert[Y](yt: Try[Y]): Future[Y]  = {
				  val yp = Promise[Y]()
					yt match {
						  case Success(y) => yp complete Try(y)
						  case Failure(e) => yp complete Try(throw e)
				  }
				  yp.future
		  }
    for (xt <- xtf; x <- convert(xt)) yield x
  }

  def flatten[X](xft : Try[Future[X]]): Future[X] =
    xft match {
      case Success(xf) => xf
      case Failure(e) => {
        (Promise[X]() complete (throw e)).future
      }
    }

}