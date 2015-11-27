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

class ApplicationRational extends ApplicationBase[Rational] {
  val name = "ScalaRPN (Rational)"
  implicit val system = ActorSystem("RPN-Calculator")    
  val calculator = system.actorOf(Props.create(classOf[RationalCalculator]))

  def index: Action[AnyContent] = index(calculator, name)

  def command(s: String): Action[AnyContent] = command(calculator, s, name)

}
