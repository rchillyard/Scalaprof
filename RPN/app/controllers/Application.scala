package controllers

import play.api._
import play.api.mvc._
import actors.Calculator
import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent._
import scala.concurrent.duration._
import scala.util._
import actors.View
import actors.RationalCalculator
import edu.neu.coe.scala.numerics.Rational
import akka.actor.ActorRef
import com.typesafe.config.{ ConfigFactory, Config }


class Application extends Controller {
  
//  val config = ConfigFactory.load()
//  val classCalculator = config.getString("actor.calculator.class")
  
  val appRat: ApplicationBase[_] = new ApplicationRational()
  val appDub: ApplicationBase[_] = new ApplicationDouble()

  def index: Action[AnyContent] = appRat.index

  def command(s: String): Action[AnyContent] = appRat.command(s)

}
