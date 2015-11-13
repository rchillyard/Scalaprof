package edu.neu.coe.scala.list

import scala.annotation.tailrec

trait List[+A] {
  override def toString = {
    @tailrec def tos(as: List[A], r: StringBuffer): CharSequence = as match {
      case Nil => r
      case Cons(hd,tl) => tos(tl,r.append((if (r.length>1) ", " else "") + s""""$hd"""")) 
      }
    tos(this,new StringBuffer("("))+")"
  }
  def ++[B >: A](x: List[B]): List[B] = this match {
    case Nil => x
    case Cons(hd,tl) => Cons(hd, tl ++ x)
  }
	def length: Int = {
		@tailrec def len(as: List[A], x: Int): Int = as match {
			case Nil => x
			case Cons(hd,tl) => len(tl,x+1)
		}
		len(this,0)
  }
  def isEmpty: Boolean = this match {case Nil => true; case _ => false }
//  def x2: A = {
//    @tailrec def internal(as: List[A], x: A): A = as match {
//      case Nil => x
//      case Cons(hd,tl) => internal(tl,x++hd)
//    }
//    internal(this,0)
//  }
  def x3: List[A] = this match {case Nil => Nil; case Cons(hd,tl) => tl}
  def x3a: Option[A] = this match {case Nil => None; case Cons(hd,tl) => Some(hd)}
  def apply(idx: Int): A = {
    @tailrec def internal(as: List[A], x: Int): A = as match {
      case Nil => throw new IndexOutOfBoundsException(s"index out of bounds: $idx")
      case Cons(hd,tl) => if (x==0) hd else internal(tl,x-1)
    }
    if (idx>=0) internal(this,idx) else throw new IndexOutOfBoundsException(s"index out of bounds: $idx")
  }
  def filter(f: A=>Boolean): List[A] = this match {
    case Cons(hd,tl) => val ftl = tl.filter(f); if (f(hd)) Cons(hd, ftl) else ftl
    case Nil => Nil
  }
  def find(f: A=>Boolean): Option[A] = this match {
    case Cons(hd,tl) => if (f(hd)) Some(hd) else tl.find(f)
    case Nil => None
  }
  def map[B](f: A=>B): List[B] = this match {case Cons(hd,tl) => Cons(f(hd),tl.map(f)); case Nil => List[B]() }
  def flatMap[B](f: A=>List[B]): List[B] = this match {
    case Cons(hd,tl) => f(hd) ++ tl.flatMap(f)
    case Nil => List[B]()
  }
  def foreach(f: A=>Unit): Unit = this match {
    case Cons(hd,tl) => f(hd); tl.foreach(f)
    case Nil => Unit
  }
  def count(f: Counter[A]): List[Int] = this map f

  def foldLeft[B](z: B)(f: (B,A)=>B): B = this match {
    case Nil => z
    case Cons(hd,tl) => tl.foldLeft(f(z,hd))(f)
  }
  
//  def sum[B]: B = foldLeft(B.unit)(B.plus(_,_))
}
case object Nil extends List[Nothing]

case class Cons[+A] (head: A, tail: List[A]) extends List[A] {
  def equals[B >: A](z: List[B]): Boolean = tail match {
    case Nil => false
    case Cons(x,xs) => z match {
      case Cons(y,ys) => x==y && xs==ys
    }
  }
}

abstract class Counter[-A] extends Function1[A,Int]

object List {
  type IntList = List[Int]
  def sum(ints: IntList): Int = ints match {
    case Nil => 0
    case Cons(x,xs) => x + sum(xs)
  }
  
  def apply[A](as: A*): List[A] = 
    if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))
}

trait Functor[F[_]] {
  def map[A,B](fa: F[A])(f: A => B): F[B]
}
