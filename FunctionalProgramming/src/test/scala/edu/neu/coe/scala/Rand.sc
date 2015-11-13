package edu.neu.coe.scala

object Rand {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
trait RNG { def nextInt: (Int,RNG) }
type Rand[+A] = RNG => (A, RNG)
val int: Rand[Int] = _.nextInt                    //> int  : edu.neu.coe.scala.Rand.Rand[Int] = <function1>
int                                               //> res0: edu.neu.coe.scala.Rand.Rand[Int] = <function1>
case class SimpleRNG(seed: Long) extends RNG {
     def nextInt: (Int, RNG) = {
     val newSeed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL
     val nextRNG = SimpleRNG(newSeed)
     val n = (newSeed >>> 16).toInt
     (n, nextRNG)
     }
     }
val rng1 = SimpleRNG(42)                          //> rng1  : edu.neu.coe.scala.Rand.SimpleRNG = SimpleRNG(42)
rng1.nextInt                                      //> res1: (Int, edu.neu.coe.scala.Rand.RNG) = (16159453,SimpleRNG(1059025964525)
                                                  //| )
int(rng1)                                         //> res2: (Int, edu.neu.coe.scala.Rand.RNG) = (16159453,SimpleRNG(1059025964525)
                                                  //| )
val (n1, rng2a) = rng1.nextInt                    //> n1  : Int = 16159453
                                                  //| rng2a  : edu.neu.coe.scala.Rand.RNG = SimpleRNG(1059025964525)
def unit[A](a: A): Rand[A] = (a, _)               //> unit: [A](a: A)edu.neu.coe.scala.Rand.Rand[A]
unit(1)                                           //> res3: edu.neu.coe.scala.Rand.Rand[Int] = <function1>
unit(1)(rng1)                                     //> res4: (Int, edu.neu.coe.scala.Rand.RNG) = (1,SimpleRNG(42))
def unit1[A](a: A): Rand[A] = rng => (a, rng)     //> unit1: [A](a: A)edu.neu.coe.scala.Rand.Rand[A]
unit1(1)(rng1)                                    //> res5: (Int, edu.neu.coe.scala.Rand.RNG) = (1,SimpleRNG(42))
def map[A,B](s: Rand[A])(f: A => B): Rand[B] =
     rng => { val (a, rng2) = s(rng); (f(a), rng2) }
                                                  //> map: [A, B](s: edu.neu.coe.scala.Rand.Rand[A])(f: A => B)edu.neu.coe.scala.R
                                                  //| and.Rand[B]
def nonNegativeInt(rng: RNG): (Int, RNG) = {
     val (i,r) = rng.nextInt
     (math.abs(i),r)
     }                                            //> nonNegativeInt: (rng: edu.neu.coe.scala.Rand.RNG)(Int, edu.neu.coe.scala.Ran
                                                  //| d.RNG)
def nonNegativeEven: Rand[Int] = map(nonNegativeInt)(i => i - 1 % 2)
                                                  //> nonNegativeEven: => edu.neu.coe.scala.Rand.Rand[Int]
val (i2, rng2) = rng1.nextInt                     //> i2  : Int = 16159453
                                                  //| rng2  : edu.neu.coe.scala.Rand.RNG = SimpleRNG(1059025964525)
val (i3, rng3) = rng2.nextInt                     //> i3  : Int = -1281479697
                                                  //| rng3  : edu.neu.coe.scala.Rand.RNG = SimpleRNG(197491923327988)
nonNegativeEven(rng2)                             //> res6: (Int, edu.neu.coe.scala.Rand.RNG) = (1281479696,SimpleRNG(197491923327
                                                  //| 988))
def flatMap[A,B](f: Rand[A])(g: A => Rand[B]): Rand[B] = {
     rng => { val (a, rng2) = f(rng); g(a)(rng2) }
     }                                            //> flatMap: [A, B](f: edu.neu.coe.scala.Rand.Rand[A])(g: A => edu.neu.coe.scal
                                                  //| a.Rand.Rand[B])edu.neu.coe.scala.Rand.Rand[B]
def mapa[A,B](s: Rand[A])(f: A => B): Rand[B] = flatMap(s)(a => unit(f(a)))
                                                  //> mapa: [A, B](s: edu.neu.coe.scala.Rand.Rand[A])(f: A => B)edu.neu.coe.scala
                                                  //| .Rand.Rand[B]
def nonNegativeEvena: Rand[Int] = mapa(nonNegativeInt)(i => i - 1 % 2)
                                                  //> nonNegativeEvena: => edu.neu.coe.scala.Rand.Rand[Int]
nonNegativeEvena(rng2)                            //> res7: (Int, edu.neu.coe.scala.Rand.RNG) = (1281479696,SimpleRNG(19749192332
                                                  //| 7988))
}