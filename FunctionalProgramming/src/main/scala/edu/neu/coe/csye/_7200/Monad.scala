package edu.neu.coe.csye._7200

trait Monad[+A] {
  def map[B, C](f: A => B): C
  def flatMap[B, C](f: A => scala.collection.GenTraversableOnce[B]): C
  def foreach[U](f: A => U): Unit
}