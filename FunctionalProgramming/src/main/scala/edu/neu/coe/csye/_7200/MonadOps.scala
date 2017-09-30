package edu.neu.coe.csye._7200

import scala.concurrent.{ExecutionContext, Future}
import scala.util._
;

/**
  * @author scalaprof
  */
object MonadOps {

  // TODO implement. 6 points. Hint: write as a for-comprehension, using the method asFuture (below).
  def flatten[X](xyf: Future[Try[X]])(implicit executor: ExecutionContext): Future[X] = ???

  def flatten[X](xfy: Try[Future[X]]): Future[X] =
    xfy match {
      case Success(xf) => xf
      case Failure(e) => Future.failed(e)
    }

  // TODO implement. 6 points. Hint: write as a for-comprehension, using the method Future.sequence
  def flatten[X](xsfs: Seq[Future[Seq[X]]])(implicit ec: ExecutionContext): Future[Seq[X]] = ???

  def flattenRecover[X](esf: Future[Seq[Either[Throwable, Seq[X]]]], f: => Throwable => Unit)(implicit executor: ExecutionContext): Future[Seq[X]] = {
    def filter(uses: Seq[Either[Throwable, Seq[X]]]): Seq[X] = {
      val uses2 = for {use <- uses; if (use match {
        case Left(x) => f(x); false;
        case _ => true
      })} yield use
      val uss = for {use <- uses2; uso = sequence(use); us <- uso} yield us
      uss flatten
    }

    for {es <- esf; e = filter(es)} yield e
  }

  def flatten[K, V](voKm: Map[K, Option[V]]): Map[K, V] = for ((k, vo) <- voKm; v <- vo) yield k -> v


  def asFuture[X](xy: Try[X]): Future[X] = xy match {
    case Success(s) => Future.successful(s)
    case Failure(e) => Future.failed(e)
  }

  // TODO implement. 4 points.
  def sequence[X](xy: Try[X]): Either[Throwable, X] = ???

  def sequence[X](xf: Future[X])(implicit executor: ExecutionContext): Future[Either[Throwable, X]] =
    xf transform( { s => Right(s) }, { f => f }) recoverWith[Either[Throwable, X]] { case f => Future(Left(f)) }

  // TODO implement. 6 points. Hint: write as a for-comprehension, using the method sequence (above).
  def sequence[X](xfs: Seq[Future[X]])(implicit executor: ExecutionContext): Seq[Future[Either[Throwable, X]]] = ???

  def sequence[X](xys: Seq[Try[X]]): Try[Seq[X]] = (Try(Seq[X]()) /: xys) {
    (xsy, xy) => for (xs <- xsy; x <- xy) yield xs :+ x
  }

  def sequence[X](xys: Stream[Try[X]]): Try[Stream[X]] = (Try(Stream[X]()) /: xys) {
    (xsy, xy) => for (xs <- xsy; x <- xy) yield xs :+ x
  }

  def sequence[X](xos: Seq[Option[X]]): Option[Seq[X]] = (Option(Seq[X]()) /: xos) {
    (xso, xo) => for (xs <- xso; x <- xo) yield xs :+ x
  }

  // TODO implement. 7 points. This one is a little more tricky. Remember what I mentioned about Either not being a pure monad -- it needs projecting
  def sequence[X](xe: Either[Throwable, X]): Option[X] = ???

  def zip[A, B](ao: Option[A], bo: Option[B]): Option[(A, B)] = for (a <- ao; b <- bo) yield (a, b)

  def optionToTry[X](xo: Option[X], t: => Throwable): Try[X] = Try(xo.get).recoverWith { case _: java.util.NoSuchElementException => Failure[X](t) }

  def optionToTry[X](xo: Option[X]): Try[X] = Try(xo.get)

  def map2[T, U](ty1: Try[T], ty2: Try[T])(f: (T, T) => U): Try[U] = for {t1 <- ty1; t2 <- ty2} yield f(t1, t2)

  def liftTry[T, U](f: T => U): Try[T] => Try[U] = _ map f

  def liftOption[T, U](f: T => U): Option[T] => Option[U] = _ map f

  def liftFuture[T, U](f: T => U)(implicit executor: ExecutionContext): Future[T] => Future[U] = _ map f
}
