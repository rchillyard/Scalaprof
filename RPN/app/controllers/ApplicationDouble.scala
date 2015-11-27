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
import actors.Calculator

class ApplicationDouble extends ApplicationBase[Double] {
  val name = "ScalaRPN (Double)"
  implicit val system = ActorSystem("RPN-Calculator")    
  val calculator = system.actorOf(Props.create(classOf[Calculator]))

  def index: Action[AnyContent] = index(calculator, name)

  def command(s: String): Action[AnyContent] = command(calculator, s, name)

}
