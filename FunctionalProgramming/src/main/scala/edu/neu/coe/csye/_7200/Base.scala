package edu.neu.coe.csye._7200


trait Base {
  type T
  def method: T
}

class Dog extends Base {
  type T = String
  def method: String = "woof!"
}