package CSYE7200

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by scalaprof on 8/25/16.
  */
class HelloWorldSpec extends FlatSpec with Matchers {

  behavior of "HelloWorld"

  it should "get the correct greeting" in {
    val greeting = HelloWorld.greeting
    greeting shouldBe "Hello World!"
  }
}
