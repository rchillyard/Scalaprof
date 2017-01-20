package com.phasmid.hedge_fund.http

import spray.http._
import spray.client.pipelining._
import java.net.URL
import java.text.SimpleDateFormat
import java.util.{ Locale, Date }
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.{ Actor, ActorSystem, Props, ActorRef }
import akka.event.Logging
import scala.concurrent._
import scala.util.{ Try, Success, Failure }
import java.io._
import akka.actor.actorRef2Scala
import com.phasmid.hedge_fund.actors.HttpResult

/**
 * CONSIDER making this an Actor
 * @author robinhillyard
 */
case class HttpTransaction(queryProtocol: String, request: HttpRequest, actor: ActorRef) {
  import akka.pattern.pipe

  implicit val system = ActorSystem()

  val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

  val response: Future[HttpResponse] = pipeline(request)

  response map { x => HttpResult(queryProtocol, request, x) } pipeTo actor

}

