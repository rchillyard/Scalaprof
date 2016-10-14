package edu.neu.coe.scala.poets

import scala.xml.{XML, Node, NodeSeq}

case class Name(name: String, language: String) { 
  def toXML = <name language={language}>{name}</name>
}

case class Poet(names: Seq[Name]) {
  def toXML = <poet>{names map (_.toXML)}</poet>  
}

object Poet {
  def fromXML(node: Node) = Poet(Name.fromXML(node \ "name")) 
}

object Name {
  def getLanguage(x: Option[Seq[Node]]) = x match {case Some(Seq(y)) => y.text; case _ => ""} 
  def fromXML(nodes: NodeSeq): Seq[Name] = for {
    node <- nodes
  } yield Name(node.text,getLanguage(node.attribute("language")))
}



/**
 * @author scalaprof
 */
object Poets extends App {
  import spray.json._
  type PoetSeq = Seq[Poet]
  def toXML(poets: PoetSeq) = poets map {_ toXML}
  val xml = XML.loadFile("poets.xml")
  val poets: PoetSeq = for ( poet <- xml \\ "poet" ) yield Poet.fromXML(poet)

  case class Poets(poets: PoetSeq)

  object PoetsJsonProtocol extends DefaultJsonProtocol {
      implicit val nameFormat = jsonFormat2(Name.apply)
      implicit val poetFormat = ??? // TODO 5 points
      implicit val poetsFormat = jsonFormat1(Poets)
  }

  import PoetsJsonProtocol._

  ??? // TODO 25 points. Write poets out as Json. Show the Json in the console...
      // ...Read the Json file back as poets1 and write that out as XML. Show it on console.
      // Show the comparison of the XML file you produced with the poets.xml file (as part of your submission).
}