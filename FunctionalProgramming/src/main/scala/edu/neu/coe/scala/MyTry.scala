package edu.neu.coe.scala

import scala.util._

/**
  * Created by scalaprof on 9/15/16.
  */
class MyTry {
  def getTime: Try[Long] = {
    val l: Long = System.currentTimeMillis
    if (l % 2 == 0) Failure(new Exception("time was was even"))
    else Success(l)
  }
}

object MyTry extends App {
  val ty = new MyTry().getTime
  ty foreach {System.out.println(_)}
}
