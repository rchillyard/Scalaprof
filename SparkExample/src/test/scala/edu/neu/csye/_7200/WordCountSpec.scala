package edu.neu.csye._7200

import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class WordCountSpec extends FlatSpec with Matchers with BeforeAndAfter  {

  private var sc: SparkContext = _

  before {
    sc = new SparkContext(new SparkConf().setAppName("WordCount").setMaster("local[*]"))
  }

  after {
    if (sc != null) {
      sc.stop()
    }
  }

  "result" should "right for wordCount" in {
    WordCount.wordCount(sc.textFile("input//WordCount.txt")," ").collect() should matchPattern {
      case Array(("Hello",3),("World",3),("Hi",1)) =>
    }
  }
}
