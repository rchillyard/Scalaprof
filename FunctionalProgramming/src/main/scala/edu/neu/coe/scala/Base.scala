package edu.neu.coe.scala


trait Base {
  type T
  def method: T
}

class Dog extends Base {
  type T = String
  def method: String = "woof!"
}