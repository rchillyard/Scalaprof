package edu.neu.coe.csye._7200

import scala.language.higherKinds

trait Monoid[A] {
  def op(a1: A, a2: A): A
  def zero: A
}

trait Functor[F[_]] {
   def map[A, B](f: A => B): F[B]
   def lift[A,B](f: A => B): List[A] => List[B] = _ map f
}
object Functor {
  def listFunctor = new Functor[List] {
    def map[A,B](f: A => B): List[B] = this map f
  }
}
trait Foldable[F[_]] extends Functor[F] {
  def foldLeft[A,B](z: B)(f: (B, A) => B): B
  def foldRight[A,B](z: B)(f: (A, B) => B): B
}

//trait Foldable[F[_]] extends Functor[F] {
//  def foldRight[A,B](as: F[A])(z: B)(f: (A,B)=>B): B
//  def foldLeft [A,B](as: F[A])(z: B)(f: (B,A)=>B): B
//}
object Monoid {
  type IntList = List[Int]
  
  val stringMonoid = new Monoid[String] {
    def op(a1: String, a2: String) = a1 + a2
    val zero = ""
  }
  def listMonoid[A] = new Monoid[List[A]] {
    def op(a1: List[A], a2: List[A]) = a1 ++ a2
    val zero = Nil
  }
  def foldableList[A] = new Foldable[List] {
      // TODO looks like problems with type name shadowing here
      def map[A,B](f: A => B): List[B] = ???
      def foldLeft[A,B](z: B)(f: (B, A) => B): B = ??? // tail recursive
      def foldRight[A,B](z: B)(f: (A, B) => B): B = ??? //  NOT tail recursive
  }
}

//trait Monad[F[_]] extends Functor[F] {
//  def unit[A](a: => A): F[A]
//  def flatMap[A,B](ma: F[A])(f: A=>F[B]): F[B]
//  def map[A,B](ma: F[A])(f: A=>B): F[B] = flatMap(ma)(a => unit(f(a)))
//  def map2[A,B,C](ma: F[A], mb: F[B])(f: (A,B)=>C): F[C] = flatMap(ma)(a => map(mb)(b => f(a,b)))
//}