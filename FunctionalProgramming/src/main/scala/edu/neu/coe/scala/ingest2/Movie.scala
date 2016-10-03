package edu.neu.coe.scala.ingest2

import edu.neu.coe.scala.ingest.{Ingest, Ingestible}

import scala.collection.mutable
import scala.io.{BufferedSource, Source}
import scala.util._

/**
  * This is a variation on the previous Movie class (in edu.neu.coe.scala.ingest)
  * This class represents a Movie from the IMDB data file on Kaggle.
  * Although the limitation on 22 fields in a case class has partially gone away, it's still convenient to group the different attributes together into logical classes.
  *
  * Created by scalaprof on 9/12/16.
  */
case class Movie(title: String, format: Try[Format], production: Try[Production], reviews: Try[Reviews], director: Principal, actor1: Principal, actor2: Principal, actor3: Principal, genres: Seq[String], plotKeywords: Seq[String], imdb: String)

/**
  * The movie format (including language and duration).
  *
  * @param color       whether filmed in color
  * @param language    the native language of the characters
  * @param aspectRatio the aspect ratio of the film
  * @param duration    its length in minutes
  */
case class Format(color: Boolean, language: String, aspectRatio: Double, duration: Int) {
  override def toString = {
    val x = color match {
      case true => "Color";
      case _ => "B&W"
    };
    s"$x,$language,$aspectRatio,$duration"
  }
}

/**
  * The production: its country, year, and financials
  *
  * @param country   country of origin
  * @param budget    production budget in US dollars
  * @param gross     gross earnings (?)
  * @param titleYear the year the title was registered (?)
  */
case class Production(country: String, budget: Int, gross: Int, titleYear: Int) {
  def isKiwi() = this match {
    case Production("New Zealand", _, _, _) => true
    case _ => false
  }
}

/**
  * Information about various forms of review, including the content rating.
  */
case class Reviews(imdbScore: Double, facebookLikes: Int, contentRating: Rating, numUsersReview: Int, numUsersVoted: Int, numCriticReviews: Int, totalFacebookLikes: Int)

/**
  * A cast or crew principal
  *
  * @param name          name
  * @param facebookLikes number of FaceBook likes
  */
case class Principal(name: Name, facebookLikes: Int) {
  override def toString = s"$name ($facebookLikes likes)"
}

/**
  * A name of a contributor to the production
  *
  * @param first  first name
  * @param middle middle name or initial
  * @param last   last name
  * @param suffix suffix
  */
case class Name(first: String, middle: Option[String], last: String, suffix: Option[String]) {
  override def toString = {
    case class Result(r: StringBuffer) { def append(s: String): Unit = r.append(" "+s); override def toString = r.toString}
    val r: Result = Result(new StringBuffer(first))
    middle foreach {r.append(_)}
    r.append(last)
    suffix foreach {r.append(_)}
    r.toString
  }
}

/**
  * The US rating
  */
case class Rating(code: String, age: Option[Int]) {
  override def toString = code + (age match {
    case Some(x) => "-" + x
    case _ => ""
  })
}

object Movie extends App {

  trait IngestibleMovie extends Ingestible[Movie] {
    def fromString(w: String): Try[Movie] = Try(Movie(w.split(",").toSeq))
  }

  implicit object IngestibleMovie extends IngestibleMovie

  val ingester = new Ingest[Movie]()
  if (args.length > 0) {
    val source = Source.fromFile(args.head)
    val kiwiMovies = getMoviesFromCountry(source,"New Zealand")
    kiwiMovies foreach { _ foreach { println(_) } }
    source.close()
  }

  def getMoviesFromCountry(source: BufferedSource, country: String): Iterator[Try[Movie]] = {
    for (my <- ingester(source)) yield
      // TODO 12 points -- using for comprehension based on pattern match (NOT a filter) -- and see Assignment4 for important hint
      ???
  }

  /**
    * Form a list from the elements explicitly specified (by position) from the given list
    *
    * @param list    a list of Strings
    * @param indices a variable number of index values for the desired elements
    * @return a list of Strings containing the specified elements in order
    */
  def elements(list: Seq[String], indices: Int*): List[String] = {
    val x = mutable.ListBuffer[String]()
    for (i <- indices) x += list(i)
    x.toList
  }

  /**
    * Alternative apply method for the Movie class
    *
    * @param ws a sequence of Strings
    * @return a Movie
    */
  def apply(ws: Seq[String]): Movie = {
    // we ignore facenumber_in_poster.
    val title = ws(11)
    val format = Format.parse(elements(ws, 0, 19, 26, 3))
    val production = Production.parse(elements(ws, 20, 22, 8, 23))
    val reviews = Reviews.parse(elements(ws, 25, 27, 21, 18, 12, 2, 13))
    val director = Principal(elements(ws, 1, 4))
    val actor1 = Principal(elements(ws, 10, 7))
    val actor2 = Principal(elements(ws, 6, 24))
    val actor3 = Principal(elements(ws, 14, 5))
    val plotKeywords = ws(16).split("""\|""").toList
    val genres = ws(9).split("""\|""").toList
    val imdb = ws(17)
    Movie(title, format, production, reviews, director, actor1, actor2, actor3, genres, plotKeywords, imdb)
  }
}

object Format {
  def parse(params: List[String]): Try[Format] = params match {
    case color :: language :: aspectRatio :: duration :: Nil =>
      for (f <- fy(Try(duration.toInt), Try(aspectRatio.toDouble))) yield f(language)(color == "Color")
    case _ => throw new Exception(s"logic error in Format: $params")
  }

  import Function._
  val fy = lift2(uncurried2(invert4((apply _).curried)))
}

object Production {
  def parse(params: List[String]): Try[Production] = params match {
    case country :: budget :: gross :: titleYear :: Nil =>
      for (f <- fy(Try(titleYear.toInt), Try(gross.toInt), Try(budget.toInt))) yield f(country)
    case _ => throw new Exception(s"logic error in Production: $params")
  }

  import Function._
  val fy = lift3(uncurried3(invert4((apply _).curried)))
}

object Reviews {
  def parse(imdbScore: Try[Double], facebookLikes: Try[Int], contentRating: Try[Rating], numUsersReview: Try[Int], numUsersVoted: Try[Int], numCriticReviews: Try[Int], totalFacebookLikes: Try[Int]): Try[Reviews] =
    Function.map7(imdbScore, facebookLikes, contentRating, numUsersReview, numUsersVoted, numCriticReviews, totalFacebookLikes)(Reviews.apply)

  def parse(params: List[String]): Try[Reviews] = params match {
    case imdbScore :: facebookLikes :: contentRating :: numUsersReview :: numUsersVoted :: numCriticReviews :: totalFacebookLikes :: Nil => parse(Try(imdbScore.toDouble), Try(facebookLikes.toInt), Try(Rating(contentRating)), Try(numUsersReview.toInt), Try(numUsersVoted.toInt), Try(numCriticReviews.toInt), Try(totalFacebookLikes.toInt))
    case _ => Failure(new Exception(s"logic error in Reviews: $params"))
  }
}

object Name {
  // XXX this regex will not parse all names in the Movie database correctly. Still, it gets most of them.
  val rName = """^([\p{L}\-\']+\.?)\s*(([\p{L}\-]+\.)\s)?([\p{L}\-\']+\.?)(\s([\p{L}\-]+\.?))?$""".r

  def apply(name: String): Name = name match {
    case rName(first, _, null, last, _, null) => apply(first, None, last, None)
    case rName(first, _, middle, last, _, null) => apply(first, Some(middle), last, None)
    case rName(first, _, null, last, _, suffix) => apply(first, None, last, Some(suffix))
    case rName(first, _, middle, last, _, suffix) => apply(first, Some(middle), last, Some(suffix))
    case _ => throw new Exception(s"parse error in Name: $name")
  }
}

object Principal {
  def apply(params: List[String]): Principal = params match {
    case name :: facebookLikes :: Nil => apply(name, facebookLikes.toInt)
    case _ => throw new Exception(s"logic error in Principal: $params")
  }

  def apply(name: String, facebookLikes: Int): Principal = apply(Name(name), facebookLikes)
}

object Rating {
  val rRating = """^(\w*)(-(\d\d))?$""".r

  /**
    * Alternative apply method for the Rating class such that a single String is decoded
    *
    * @param s a String made up of a code, optionally followed by a dash and a number, e.g. "R" or "PG-13"
    * @return a Rating
    */
  def apply(s: String): Rating =
    s match {
      case rRating(code, _, age) => apply(code, Try(age.toInt).toOption)
      case _ => throw new Exception(s"parse error in Rating: $s")
    }
}
