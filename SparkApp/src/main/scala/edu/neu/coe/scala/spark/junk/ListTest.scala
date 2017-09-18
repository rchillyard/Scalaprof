package edu.neu.coe.scala.spark.junk

import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ListBuffer

object ListTest extends App {

  val conf = new SparkConf().setAppName("spam")
  val sc = new SparkContext(conf)

  val propertyData = sc.textFile("listproperty.conf")

  val propertyList = new ListBuffer[(String,String)]()

  propertyData.foreach { line =>
    val c = line.split("=")
    propertyList.append((c(0), c(1)))
  }

  println(propertyList)

}
