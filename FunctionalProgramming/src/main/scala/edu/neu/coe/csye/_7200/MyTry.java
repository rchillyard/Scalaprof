package edu.neu.coe.csye._7200;

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
