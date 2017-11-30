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

  behavior of "myFilter"

  it should "work" in {
    WordCount.myFilter("Hello","He") shouldBe false
    WordCount.myFilter("Hello","he") shouldBe true
  }

  behavior of "myReplace"

  it should "work" in {
    WordCount.myReplacer("abc,") shouldBe "abc"
    WordCount.myReplacer("abc") shouldBe "abc"
  }

  behavior of "Spark"

  it should "work for wordCount" in {
    WordCount.wordCount(spark.read.textFile("input//WordCount.txt").rdd," ").collect() should matchPattern {
      case Array(("Hello",3),("World",3),("Hi",1)) =>
    }
  }

  it should "work for wordCount2" in {
    WordCount.wordCount2(spark.read.textFile("input//WordCount2.txt").rdd," ").collect() should matchPattern {
      case Array(("hi",2), ("hello",1), ("and",1), ("world",2)) =>
    }
  }

  it should "work for wordCount3" in {
    WordCount.wordCount3(spark.read.textFile("input//WordCount2.txt").rdd," ").collect() should matchPattern {
      case Array(("hi",2), ("hello",1), ("and",1), ("world",2)) =>
    }
  }

  it should "work for Dataset and Spark SQL" in {
    val ds = spark.read.textFile("input//WordCount.txt")
    val words = WordCount.createWordDS(ds," ")
    words.createTempView("words")
    words.cache()
    spark.sql("select count(*) from words").head().getLong(0) shouldBe 7
    spark.sql("select word, count(*) from words group by word").collect().map(_.toString()) shouldBe
      Array("[World,3]","[Hi,1]","[Hello,3]")
  }

}
