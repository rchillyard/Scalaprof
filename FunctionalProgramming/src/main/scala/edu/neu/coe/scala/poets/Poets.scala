package edu.neu.coe.scala.poets

import scala.xml.{XML, Node, NodeSeq}
//import DefaultJsonProtocol._

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
  println(poets)  
  println(toXML(poets))
  
  case class Poets(poets: PoetSeq)

  object PoetsJsonProtocol extends DefaultJsonProtocol {
      implicit val nameFormat = jsonFormat2(Name.apply)
      implicit val poetFormat = jsonFormat1(Poet.apply)
      implicit val poetsFormat = jsonFormat1(Poets)
  }

  import PoetsJsonProtocol._
  
  println("JSON: "+poets.toJson)

  def fromJson (string: String) = string.parseJson.convertTo[PoetSeq]
  
  val source = """[{"names":[{"name":"Wang Wei","language":"en"},{"name":"王維","language":"zh"}]},{"names":[{"name":"Li Bai","language":"en"},{"name":"李白","language":"zh"}]}]"""
  
  val x = fromJson(source)
  
  println(x)
}