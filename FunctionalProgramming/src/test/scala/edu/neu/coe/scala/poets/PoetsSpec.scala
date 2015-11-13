package edu.neu.coe.scala.poets

import org.scalatest.{ FlatSpec, Matchers }
import edu.neu.coe.scala.poets._

/**
 * @author scalaprof
 */
class PoetsSpec extends FlatSpec with Matchers {
  
  "toXML" should "work for Li Bai" in {
      val xml = <poet><name language="en">Li Bai</name><name language="zh">李白</name></poet>
      val liBai = Poet.fromXML(xml)
      val xmlString = liBai.toXML
      xmlString should equal (<poet><name language="en">Li Bai</name><name language="zh">李白</name></poet>)
  }

}