package edu.neu.coe.scala.parse

import java.io.{InputStream,File}
import scala.io.Source
import scala.util._
import scala.util.matching._
import scala.util.parsing.combinator._
import scala.collection.GenTraversableOnce
import java.net.URI
import edu.neu.coe.scala.util.Tuples
import edu.neu.coe.scala.MonadOps
import edu.neu.coe.scala.trial._

/**
 * ProductStream is a monadic trait which provides a Stream of Tuples (tuples) and a Header (header).
 * Thus it is well-suited for use as an ingest mechanism of CSV files.
 * 
 * The polymorphic type X is a Tuple of some count.
 * Please note that type inference is not able to infer this type from reading the file (which doesn't happen until runtime).
 * Therefore, the caller must supply the type of X.
 * Please see ProductStreamSpec for exemplars.
 * 
 * Please see inline method documentation for details of other methods. 
 * 
 * @author scalaprof
 *
 * @param <X>
 */
trait ProductStream[X <: Product] {
  /**
	 * @return a sequence of String corresponding to the column names (left to right)
	 */
  def header: Seq[String]
  /**
	 * @return a Stream of tuples
	 */
  def tuples: Stream[X]
  /**
	 * @return a materialized (non-lazy) List version of the tuples.
	 */
  lazy val asList = tuples.toList
  /**
   * map method
	 * @param f function to be applied to each tuple
	 * @return a ProductStream of transformed tuples
	 */
  def map[Y <: Product](f: X=>Y): ProductStream[Y] = ??? // TODO Assignment6 5
  /**
   * flatMap method
	 * @param f function to be applied to each tuple
	 * @return a ProductStream of transformed tuples
	 */
  def flatMap[Y <: Product](f: X=>GenTraversableOnce[Y]): ProductStream[Y] = ??? // TODO Assignment6 5
  /**
   * toMap method
	 * @param pk function to yield a primary key value from a tuple
	 * @return a Map where each element is of form pk->tuple
	 */
  def toMap[K](pk: X=>K): Map[K,X] = (for {x <- asList } yield (pk(x)->x)).toMap
  /**
	 * @param i get the ith row as a tuple
	 * @return Some(tuple) if i is valid, else None
	 */
  def get(i: Int): Option[X] = if (i>=0 && i<asList.size) Some(asList.apply(i)) else None
  /**
	 * @return a Stream of Maps, each map corresponding to a row, such that the keys are the column names (from the header)
	 * and the values are the tuple values
	 */
  def asMaps: Stream[Map[String,Any]]
}

/**
 * Base class for implementers of ProductStream
 */
abstract class ProductStreamBase[X <: Product] extends ProductStream[X] {
  /**
   * Method asMaps converts this ProductStream into a Stream of Map[String,Any] objects, one per row.
   * The keys for the map are derived from the header and the values from the tuple elements.
   * @return a Stream of Map[String,Any] objects
   */
  def asMaps: Stream[Map[String,Any]] = ??? // TODO Assignment6 14
}

/**
 * Base class for ProductStream which additionally derive their header and tuples from parsing a Stream of Strings (one per row).
 */
abstract class TupleStreamBase[X <: Product](parser: CsvParser, input: Stream[String]) extends ProductStreamBase[X] {
  /**
	 * @return the header for this object
	 * @throws the exception which is wrapped in a Failure from wsy (below)
	 */
  def header: Seq[String] = wsy.get
  /**
	 * @param f the function which will be applied to a String to yield an Any (an element of a Tuple)
	 * @param s the (row/line) String to be parsed
	 * @return a Tuple
	 * @throws an exception if any of the underlying code generated a Failure
	 */
  def stringToTuple[X <: Product](f: String=>Try[Any])(s: String): X = stringToTryTuple(f)(s).get
  protected lazy val wsy: Try[Seq[String]] = parser.parseRow(input.head)
  private def stringToTryTuple[X <: Product](f: String=>Try[Any])(s: String): Try[X] =
    for {
      ws <- parser.parseRow(s)
      // Note that the following will result in a Failure[NoSuchElementException] if the filter results in false
      if (ws.size==header.size)
      // Note that the specification of [X] in the following is essential
      t <- TupleStream.seqToTuple[X](ws)(f)
    } yield t
}

/**
 * Case class which implements ProductStream where the header and tuples are specified directly
 */
case class ConcreteProductStream[X <: Product](header: Seq[String], tuples: Stream[X]) extends ProductStreamBase[X]

/**
 * Case class which implements ProductStream where the header and tuples are specified indirectly, by providing
 * a parser and Stream[String] such that the element types of the resulting tuples will be inferred from their representative
 * Strings.
 * @param X a Tuple which should correspond with the number of (and types inferred from) the values.
 */
case class CSV[X <: Product](parser: CsvParser, input: Stream[String]) extends TupleStreamBase[X](parser,input) {
  /**
   * Method to define the tuples of this TupleStreamBase object.
   * Note that the [X] following stringToTuple looks optional, but it is not!
	 * @return a Stream of [X] objects
 	*/
  def tuples = input.tail map stringToTuple[X](parser.elementParser)
  /**
   * method to project ("slice") a ProductStream into a single column 
	 * @param key the name of the column
	 * @return an Option of Stream[Y] where Y is the type of the column
 	*/
  def column[Y](key: String): Option[Stream[Y]] = column(header.indexOf(key))
  /**
   * method to project ("slice") a ProductStream into a single column 
	 * @param i the index of the column (0 on the left, n-1 on the right)
	 * @return an Option of Stream[Y] where Y is the type of the column
 	*/
  def column[Y](i: Int): Option[Stream[Y]] = 
    if (i>=0) Some(tuples map CSV.project[X,Y](i))
    else None
}

/**
 * Case class which implements ProductStream where the header and tuples are specified indirectly, by providing
 * a parser and Stream[String] such that the element types of the resulting tuples will be Strings.
 * @param X a Tuple which should correspond with the number of values (all types of the tuple should be String).
 */
case class TupleStream[X <: Product](parser: CsvParser, input: Stream[String]) extends TupleStreamBase[X](parser,input) {
  def tuples = input.tail map stringToTuple[X]{x=>Success(x)}
  /**
   * method to project ("slice") a ProductStream into a single column 
	 * @param key the name of the column
	 * @return an Option of Stream[String]
 	*/
  def column(key: String): Option[Stream[String]] = column(header.indexOf(key))
  /**
   * method to project ("slice") a ProductStream into a single column 
	 * @param i the index of the column (0 on the left, n-1 on the right)
	 * @return an Option of Stream[String]
 	*/
  def column(i: Int): Option[Stream[String]] = 
    if (i>=0) Some(tuples map TupleStream.project[X](i))
    else None
}

object TupleStream {
  def apply[X <: Product](input: Stream[String]): TupleStream[X] = apply(CsvParser(),input)
  def apply[X <: Product](parser: CsvParser, input: InputStream): TupleStream[X] = apply(parser,Source.fromInputStream(input).getLines.toStream)
  def apply[X <: Product](input: InputStream): TupleStream[X] = apply(CsvParser(),input)
  def apply[X <: Product](parser: CsvParser, input: File): TupleStream[X] = apply(parser,Source.fromFile(input).getLines.toStream)
  def apply[X <: Product](input: File): TupleStream[X] = apply(CsvParser(),input)
  def apply[X <: Product](parser: CsvParser, input: URI): TupleStream[X] = apply(parser,Source.fromFile(input).getLines.toStream)
  def apply[X <: Product](input: URI): TupleStream[X] = apply(CsvParser(),input)
  def project[X <: Product](i: Int)(x: X): String = x.productElement(i).asInstanceOf[String]
  def toTuple[X <: Product](ats: Seq[Try[Any]]): Try[X] = ??? // TODO Assignment6 8 Hint: use MonadOps.sequence; Tuples.toTuple; and asInstanceOf
  def seqToTuple[X <: Product](ws: Seq[String])(f: String=>Try[Any]): Try[X] = toTuple(ws map f)
}

object CSV {
  def apply[X <: Product](input: Stream[String]): CSV[X] = apply(CsvParser(),input)
  def apply[X <: Product](parser: CsvParser, input: InputStream): CSV[X] = apply(parser,Source.fromInputStream(input).getLines.toStream)
  def apply[X <: Product](input: InputStream): CSV[X] = apply(CsvParser(),input)
  def apply[X <: Product](parser: CsvParser, input: File): CSV[X] = apply(parser,Source.fromFile(input).getLines.toStream)
  def apply[X <: Product](input: File): CSV[X] = apply(CsvParser(),input)
  def apply[X <: Product](parser: CsvParser, input: URI): CSV[X] = apply(parser,Source.fromFile(input).getLines.toStream)
  def apply[X <: Product](input: URI): CSV[X] = apply(CsvParser(),input)
  def project[X <: Product,Y](i: Int)(x: X) = x.productElement(i).asInstanceOf[Y]
}

abstract class CsvParserBase(f: String=>Try[Any]) extends JavaTokenParsers {
  /**
	 * @return the trial function that will convert a String into Try[Any]
	 * This method is referenced only by CSV class (not by TupleStream, which does no element conversion).
	 */
  def elementParser = f
}
case class CsvParser(
    delimiter: String = ",", // delimiter separating elements within rows
    quoteChar: String = """"""", // quotation char to all strings to include literal delimiter character
    elemParser: String=>Try[Any] = CurriedTrial(CsvParser.parseDate _)(CsvParser.dateFormatStrings) :| CsvParser.parseElem _ // element parser (used only by CSV class, not by TupleStream)
  ) extends CsvParserBase(elemParser) {
  def row: Parser[List[String]] = ??? // TODO Assignment6 3: row ::= term { delimiter term }
  def term: Parser[String] = ??? // TODO Assignment6 7: term ::= quoteChar text quoteChar | text
  def parseRow(s: String): Try[List[String]] = this.parseAll(this.row,s) match {
    case this.Success(r,_) => scala.util.Success(r)
    case f @ (this.Failure(_,_) | this.Error(_,_)) => scala.util.Failure(new RuntimeException(s"cannot parse $s: $f"))
  }
}

import org.joda.time._
import org.joda.time.format._
import scala.annotation.tailrec

object CsvParser {
  val dateFormatStrings = Seq("yyyy-MM-dd","yyyy-MM-dd-hh:mm:ss.s")
  def parseDate(dfs: Seq[String])(s: String): Try[Any] = {
    @tailrec def loop(formats: Seq[DateTimeFormatter],result: Try[DateTime]): Try[DateTime] = formats match {
      case Nil => result
      case h :: t => loop(t, result orElse(Try(h.parseDateTime(s))))
    }
    loop(dfs map {DateTimeFormat.forPattern(_)},Failure(new RuntimeException(s""""$s" cannot be parsed as date""")))
  }
  def unquote(r: Regex)(s: String): Try[String] = s match {case r(w) => Success(w)}
  def elementParser(x: Nothing)(s: String): Try[Any] = parseElem(x)
  def parseElem(s: String): Try[Any] =
    {
      val quoted = """"([^"]*)"""".r
      val whole = """(\d+)""".r
      val floating = """-?(\d+(\.\d*)?|\d*\.\d+)([eE][+-]?\d+)?[fFdD]?""".r
      val truth = """(?i)^([ty]|true|yes)$""".r
      val untruth = """(?i)^([fn]|false|no)$""".r
      s match {
        case quoted(w) => Success(w)
        case whole(_) => Try(s.toInt)
        case truth(_) => Success(true)
        case untruth(_) => Success(false)
        case floating(_) | floating(_,_) | floating(_,_,_) => Try(s.toDouble)
        case _ => Success(s)
    }
  }
}

