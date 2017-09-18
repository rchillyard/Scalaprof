package edu.neu.coe.csye._7200;

/**
  * Created by scalaprof on 2/17/17.
  */
object FutureExercise extends App {

  val chunk = 1000000 // Try it first with chunk = 1000 and build up to 1000000

  def integers(i: Int, n: Int): Stream[Int] = Stream.from(i) take n

  def sum[N : Numeric](is: Stream[N]): BigInt = is.foldLeft(BigInt(0))(_+implicitly[Numeric[N]].toLong(_))

  def asyncSum(is: Stream[Int]): Future[BigInt] = Future {
    val x = sum(is)
    System.err.println(s"${is.head} is done with sum $x")
    x
  }

  val xfs = for (i <- 0 to 10) yield asyncSum(integers(i * chunk, chunk))

  val xsf: Future[IndexedSeq[BigInt]] = Future.sequence(xfs)

  val xf: Future[BigInt] = for (ls <- xsf) yield sum(ls.toStream)

  xf onComplete {
    case Success(z) => println(s"Total is: $z")
    case Failure(t) => System.err.println("An error has occured: " + t.getMessage)
  }

  Await.ready(xf, 10000 milli)

  println("goodbye")

}
