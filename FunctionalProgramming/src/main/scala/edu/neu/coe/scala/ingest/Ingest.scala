package edu.neu.coe.scala.ingest

import scala.io.Source

class Ingest[T : Ingestible] extends (Source => Iterator[T]) {
  def apply(source: Source): Iterator[T] = source.getLines.toSeq.map(e => implicitly[Ingestible[T]].fromStrings(e.split(",").toSeq)).iterator
}

trait Ingestible[X] {
  def fromStrings(ws: Seq[String]): X
}

case class Movie(properties: Seq[String])

object Ingest extends App {
  trait IngestibleMovie extends Ingestible[Movie] {
    def fromStrings(ws: Seq[String]): Movie = Movie.apply(ws)
  }
  implicit object IngestibleMovie extends IngestibleMovie

  override def main(args: Array[String]): Unit = {
    val ingester = new Ingest[Movie]()
    if (args.length>0) {
      val source = Source.fromFile(args.head)
      for (m <- ingester(source)) println(m.properties.mkString(", "))
      source.close()
    }
  }
}

