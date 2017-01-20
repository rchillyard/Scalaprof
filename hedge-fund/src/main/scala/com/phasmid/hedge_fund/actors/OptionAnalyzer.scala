package com.phasmid.hedge_fund.actors

import akka.actor.{ ActorRef, Props }
import com.phasmid.hedge_fund.rules._
import akka.event.LoggingAdapter
import com.phasmid.hedge_fund.model.Model
import scala.language.postfixOps
import com.phasmid.hedge_fund.model.MapUtils
import scala.io.Source
import scala.util.Try

/**
 * @author robinhillyard
 */
class OptionAnalyzer(blackboard: ActorRef) extends BlackboardActor(blackboard) {

  // This is mutable state information.
  // When this actor is terminated and reborn, the rules/properties will be re-read from their respective files
  var rules = Map[String, String]()
  var properties = List[Map[String, Any]]()

  override def receive = {
    case CandidateOption(model, identifier, put, optionDetails, chainDetails) =>
      log.debug("Option Analysis of identifier: {}", identifier)
      val candidate = OptionCandidate(put, model, identifier, optionDetails, chainDetails)
      if (applyRules(put, candidate)) {
        log.debug("Qualifies: sending confirmation message to blackboard")
        val attributes = MapUtils.flatten[String, Any](List("underlying") map { k => (k -> candidate(k)) } toMap)
        log.debug(s"attributes: $attributes")
        blackboard ! Confirmation(identifier, model, attributes)
      } else
        log.debug(s"$identifier does not qualify")

    case m => super.receive(m)
  }

  override def preStart() {
    rules = OptionAnalyzer.getRules(log)
    properties = OptionAnalyzer.getProperties
  }

  def getProperty(key: String, value: Any, property: String) =
    getProperties(key, value) match {
      case Some(m) => m.get(property);
      case None => None
    }

  def getProperties(key: String, value: Any) =
    properties find { p => p.get(key) match { case Some(`value`) => true; case _ => false } }

  def applyRules(put: Boolean, candidate: Candidate): Boolean = {
    candidate("underlying") match {
      case Some(u) => {
        val candidateWithProperties = candidate ++ (getProperties("Id", u) match { case Some(p) => p; case _ => Map() })
        val debugger: String=>Unit = if (log.isDebugEnabled) (log.debug _) else {x => Unit}
        parseRule(candidateWithProperties, debugger, if (put) "put" else "call")
      }
      case _ => log.warning(s"underlying is not defined for option: $candidate"); false
    }
  }

  def parseRule(m: String=>Option[Any], debugger: (String) => Unit, key: String): Boolean = {
    val wt = OptionAnalyzer.optionToTry(rules.get(key), new Exception(s"rules problem: $key doesn't define a rule"))
    parseRule(m,debugger,wt)
  }
  
  def parseRule(m: String=>Option[Any], debugger: (String) => Unit, wt: Try[String]): Boolean = {
    val parser = new RuleParser(m, debugger)
    val bt = for (w <- wt; b <- parser.parseRule(w)) yield b
    (bt recoverWith{case x => log.warning(s"parse failure: {}",x); Try(false)}).get
  }
}

object OptionAnalyzer {
  import java.io.File
  import com.typesafe.config._
  import scala.util._

  def optionToTry[T](to: Option[T], default: =>Throwable) = to match {
    case Some(t) => Success(t)
    case None => Failure(default)
  }
  def getRules(log: LoggingAdapter) = {
    val userHome = System.getProperty("user.home")
    val sRules = "rules.txt"
    val sUserRules = s"$userHome/$sRules"
    val sSysRules = s"src/main/resources/$sRules"
    val userRules = new File(sUserRules)
    val config = if (userRules.exists) ConfigFactory.parseFile(userRules) else ConfigFactory.parseFile(new File(sSysRules))
    List("put", "call") map { k =>
      {
        val r = config.getString(k)
        log.info(s"rule: $k -> $r")
        k -> r
      }
    } toMap
  }

  def getProperties = {
    val sProperties = "properties.txt"
    val sSysProperties = s"src/main/resources/$sProperties"
    val src = Source.fromFile(sSysProperties).getLines
    // First line is the header
    val headerLine = src.take(1).next
    val columns = headerLine.split(",")
    src map { l => columns zip l.split(",") toMap } toList
  }
}

/**
 * @author robinhillyard
 *
 * CONSIDER combining optionDetails and chainDetails in the caller
 */
case class OptionCandidate(put: Boolean, model: Model, id: String, optionDetails: Map[String, String], chainDetails: Map[String, Any]) extends Candidate {

  def map = Map("put" -> put) ++ chainDetails ++ optionDetails

  def identifier = id

  // CONSIDER getting rid of the identifier case since we now have a method for that
  def apply(s: String) = s match {
    case "identifier" => Some(id)
    case _ => model.getKey(s) match {
      case Some(x) => map.get(x)
      case _ => None
    }
  }

  def ++(m: Map[String, Any]) = OptionCandidate(put, model, identifier, optionDetails, chainDetails ++ m)

  override def toString = s"OptionCandidate: identifier=$identifier; map=$map"
}
