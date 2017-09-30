package edu.neu.coe.csye._7200

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
