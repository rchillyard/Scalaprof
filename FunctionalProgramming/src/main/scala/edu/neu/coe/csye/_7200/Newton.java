package edu.neu.coe.csye._7200;

/**
  * Copyright (c) Robin Hillyard (Scalaprof) on 5/20/15.
  */
case class Newton(f: Double => Double, dfbydx: Double => Double) {

  private def step(x: Double, y: Double) = x - y / dfbydx(x)

  def solve(tries: Int, threshold: Double, initial: Double): Try[Double] = {
    @tailrec def inner(r: Double, n: Int): Try[Double] = {
      val y = f(r)
      if (math.abs(y) < threshold) Success(r)
      else if (n == 0) Failure(new Exception("failed to converge"))
      else inner(step(r, y), n - 1)
    }

    inner(initial, tries)
  }
}
