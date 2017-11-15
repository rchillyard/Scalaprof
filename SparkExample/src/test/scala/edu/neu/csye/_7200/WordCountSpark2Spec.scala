package edu.neu.csye._7200

import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class WordCountSpark2Spec extends FlatSpec with Matchers with BeforeAndAfter  {

  implicit var spark: SparkSession = _

  before {
    spark = SparkSession
      .builder()
      .appName("WordCount")
      .master("local[*]")
      .getOrCreate()
  }

  after {
    if (spark != null) {
      spark.stop()
    }
  }

  "result" should "right for wordCount" in {
    WordCount.wordCount(spark.read.textFile("input//WordCount.txt").rdd," ").collect() should matchPattern {
      case Array(("Hello",3),("World",3),("Hi",1)) =>
    }
  }

  "word Dataset" should "work" in {
    val ds = spark.read.textFile("input//WordCount.txt")
    val words = WordCount.createWordDS(ds," ")
    words.createTempView("words")
    words.cache()
    spark.sql("select count(*) from words").head().getLong(0) shouldBe 7
    spark.sql("select word, count(*) from words group by word").collect().map(_.toString()) shouldBe
      Array("[World,3]","[Hi,1]","[Hello,3]")
  }

}
