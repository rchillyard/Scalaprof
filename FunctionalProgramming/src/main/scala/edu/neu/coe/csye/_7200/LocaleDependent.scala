package edu.neu.coe.scala
package scaladate

import java.util.{Date,Locale}
import java.text.DateFormat
import java.text.DateFormat._

trait LocaleDependent {
  def toStringForLocale(implicit locale: Locale): String
}

case class ScalaDate(date: Date) extends LocaleDependent {
  import ScalaDate.locale
  def toStringForLocale(implicit locale: Locale): String = getDateInstance(LONG,locale) format date
  override def toString: String = toStringForLocale(locale)
}

object ScalaDate {
  def apply(): ScalaDate = ScalaDate(new Date)
  implicit def locale = Locale.FRANCE
  def main(args: Array[String]): Unit = {
    println(apply())
  }
}

