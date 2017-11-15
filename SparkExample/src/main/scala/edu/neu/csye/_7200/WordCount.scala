package edu.neu.csye._7200

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

object WordCount extends App {

  def wordCount(lines: RDD[String],separator: String) = {
    lines.flatMap(_.split(separator))
         .map((_,1))
         .reduceByKey(_ + _)
  }

  case class Word(word: String, count: Int)

  def createWordDS(ds: Dataset[String], separator: String)(implicit spark:SparkSession) = {
    import spark.implicits._
    ds.flatMap(_.split(separator))
      .map((_,1))
      .map(Word.tupled)
      .as[Word]
  }

  //For Spark 1.0-1.9
  val sc = new SparkContext(new SparkConf().setAppName("WordCount").setMaster("local[*]"))

  wordCount(sc.textFile("input//WordCount.txt")," ").collect().foreach(println(_))

  sc.stop()

  //For Spark 2.0+
  implicit val spark = SparkSession
    .builder()
    .appName("WordCount")
    .master("local[*]")
    .getOrCreate()

  wordCount(spark.read.textFile("input//WordCount.txt").rdd," ").collect().foreach(println(_))

  //Spark SQL example
  val wordDS = createWordDS(spark.read.textFile("input//WordCount.txt")," ")

  wordDS.createTempView("words")
  wordDS.cache()

  spark.sql("select word, count(*) from words group by word").show(10)

  spark.stop()
}
