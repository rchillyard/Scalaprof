package edu.neu.coe.scala

import edu.neu.coe.scala.newton._

object NewtonWorksheet {
  println("Welcome to the Newton worksheet")      //> Welcome to the Newton worksheet
    
  val f1 = Newtonian("cos(x)-x (~1E-7)", {x => math.cos(x) - x},{x => -math.sin(x) - 1},1E-7)
                                                  //> f1  : edu.neu.coe.scala.newton.Newtonian = <function1>
	Newton(f1,1.0).solve                      //> res0: Either[String,Double] = Right(0.739085133385284)
  
  val f2 = Newtonian("sqrt(2)", {x => x*x-2},{x => 2*x},1E-7)
                                                  //> f2  : edu.neu.coe.scala.newton.Newtonian = <function1>
  Newton(f2,1.0).solve                            //> res1: Either[String,Double] = Right(1.4142135623746899)
}