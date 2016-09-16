package edu.neu.coe.scala

/**
  * Created by scalaprof on 9/15/16.
  */
class Optional {
  def getTime: Option[Long] = {
    val l: Long = System.currentTimeMillis
    if (l % 2 == 0) None
    else Some(l)
  }
}

object Optional extends App {
  val to = new Optional().getTime
  to foreach {System.out.println(_)}
}
