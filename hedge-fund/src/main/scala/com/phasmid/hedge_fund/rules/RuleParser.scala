package com.phasmid.hedge_fund.rules


import scala.util.Try
import scala.util.parsing.combinator.JavaTokenParsers

/**
 * @author scalaprof
 */
class RuleParser(m: String=>Option[Any], debugger: String=>Unit) extends JavaTokenParsers { self =>
  def this(debugger: String=>Unit) = this(Map(),debugger)
  def this() = this({x:String=>()})
  def and: (Boolean,Boolean)=>Boolean = {_&_}
  def negate: Boolean=>Boolean = {!_}
  def or: (Boolean,Boolean)=>Boolean = {_|_}

  trait Valuable[T] {
    def value: Try[T]
  }
  trait Expression extends Valuable[Boolean]
  abstract class Predicate extends Expression
  abstract class Factor extends Valuable[Any]
  abstract class NumericFactor extends Valuable[Double]
  
  def lift[T,U](ty: Try[T])(f: T => U): Try[U] = ty map f
  def map2[T,U](ty1: Try[T], ty2: Try[T])(f: (T,T) => U): Try[U] = for {t1 <- ty1; t2 <- ty2 } yield f(t1,t2)
  def parseRule(s: String): Try[Boolean] = {
    parseAll(expr, s) match {
      case this.Success(p,_) => p.value
      case this.Failure(x,_) => scala.util.Failure(new Exception(s"parse failure on $s: $x"))
      case this.Error(x,_) => scala.util.Failure(new Exception(s"parse error on $s: $x"))
    }
  }
  // TODO use Numeric for these comparisons
  def cf(x: Any, y: Any): Int = x match {
    case p: Int => y match {
      case q: Int => p.compareTo(q)
      case q: Double => cf(p.toDouble,q)
      case _ => throw new Exception(s"cf(Int,_): cannot compare $x with $y")
    }
    case p: Double => y match {
      case q: Int => cf(p,q.toDouble)
      case q: Double => val r = p.compareTo(q); if (r==0) 0 else if (Math.abs(p-q)/Math.abs(p+q)<1E-7) 0 else r
      case _ => throw new Exception(s"cf(Double,_): cannot compare $x with $y")
    }
    case p: String => y match {
      case q: String => p.compareTo(q)
      case q: Int => p.toDouble.compareTo(q)
      case q: Double => p.toDouble.compareTo(q)
      case _ => throw new Exception(s"cf(String,_): cannot compare $x with $y")
    }
    case _ => throw new Exception(s"cf(_,_): cannot compare $x with $y")
  }
  case class Number(s: String, m: String) extends NumericFactor {
    def factor(m: String): Double = m match {case "B" => 1000*factor("M"); case "M" => 1000*factor("K"); case "K" => 1000*factor("1"); case "1" => 1; case _ => throw new Exception(s"invalid factor: $m")}
    def value: Try[Double] = Try(s.toDouble * factor(m))
    override def toString = s"$s"
  }
  case class Expr(t: Term, ts: List[String~Term]) extends Expression {
    def termVal(t: String~Term): Try[Boolean] = t match {case "|"~x => x.value }
    def value = ts.foldLeft(t.value)((a,x) => map2(a,termVal(x))(or))
    override def toString = ts.foldLeft(t.toString)((x,y) => x+y._1+y._2)
  }
  case class Term(f: Predicate, fs: List[String~Predicate]) extends Expression {
    def factorVal(t: String~Predicate): Try[Boolean] = t match {case "&"~x => x.value; case "!"~x => x.value map {!_} }
    def value = fs.foldLeft(f.value)((a,x)=>map2(a,factorVal(x))(and))
    override def toString = fs.foldLeft(f.toString)((x,y) => x+y._1+y._2)
  }
  case class Comparison[T](x: Valuable[T], op: String, y: Valuable[T]) extends Predicate {
    def value = {
      val f: Int=>Boolean = op match {
        case ">" => _>0
        case ">=" => _>=0
        case "<" => _<0
        case "<=" => _<=0
        case "=" => _==0
        case "!=" => _!=0
        case _ => throw new Exception(s"op: $op is not supported")
      }
      val r = lift(map2(x.value,y.value)(cf))(f)
      // TODO check that this really is called by name
      for (p <- r) debugger(s"Rule $x $op $y yields $p")
      r
    }
  }
  case class Identifier(s: Any) extends NumericFactor {
    val parser = new RuleParser
    def value: Try[Double] = m(s.toString) match {
      case Some(x) => parser.parseAll(parser.factor, x.toString) match {
          // TODO why does this need a cast?
        case parser.Success(y,_) => y.asInstanceOf[NumericFactor].value
        case _ => scala.util.Failure(new Exception(s"identifier value $x invalid"))
      }
      case None => scala.util.Failure(new Exception(s"identifier $s was not found"))
    }
    override def toString = {
      val v = value match {
        case scala.util.Success(x) => s"[=$x]"
        case _ => s"[not defined]"
      }
      "$"+s"$s$v"
    }

  }
  case class Parentheses(e: Expr) extends Predicate {
    def value = e.value
    override def toString = s"($e)"
  }
  case class Constant(s: Any) extends Predicate {
    def value = s match {
      case "Always"|"always" => Try(true)
      case "Never"|"never" => Try(false)
    }
    override def toString = s"$s"
  }
  case class FactorExpr(t: NumericFactor, ts: List[String~NumericFactor]) extends NumericFactor {
    def negate(x: Double) = -x
    def plus(x: Double, y: Double) = x+y
    def term(t: String~NumericFactor): Try[Double] = t match {case "+"~x => x.value; case "-"~x => lift(x.value)(negate)}
    def value = ts.foldLeft(t.value)((x,y) => map2(x,term(y))(plus))
    override def toString = ts.foldLeft(t.toString)((x,y) => x+y._1+y._2)
  }
  case class FactorTerm(f: NumericFactor, fs: List[String~NumericFactor]) extends NumericFactor {
    def reciprocal(x: Double) = 1/x
    def times(x: Double, y: Double) = x*y
    def factor(t: String~NumericFactor): Try[Double] = t match {case "*"~x => x.value; case "/"~x => lift(x.value)(reciprocal)}
    def value = fs.foldLeft(f.value)((x,y) => map2(x,factor(y))(times))
    override def toString = fs.foldLeft(f.toString)((x,y) => x+y._1+y._2)
  }
  val booleanOp = regex(""">|>=|<|<=|=|!=""".r)
  val identifier = regex("""\w+""".r)
  def expr: Parser[Expr] = term~rep("|"~term | failure("expr")) ^^ { case t~r => r match {case x: List[String~Term] => Expr(t,x)}}
  def term: Parser[Term] = predicate~rep("&"~predicate | "!"~predicate | failure("term")) ^^ { case f~r => Term(f,r)}
  // TODO why do we have to cast the following? Is it safe?
  def predicate: Parser[Predicate] = (constant | factor~booleanOp~factorExpr | "("~>expr<~")") ^^ { case k: Predicate => k; case x~op~y => Comparison(x.asInstanceOf[Valuable[Any]],op.toString,y.asInstanceOf[Valuable[Any]]); case e: Expr => Parentheses(e)}
  def constant: Parser[Predicate] = ("Always" | "Never") ^^ { case s => Constant(s)}
  def factorExpr: Parser[FactorExpr] = factorTerm~rep("+"~factor|"-"~factorTerm|failure("factorExpr")) ^^ { case t~r => r match {case x: List[String~NumericFactor] => FactorExpr(t,x)}}
  def factorTerm: Parser[FactorTerm] = factor~rep("*"~factor|"/"~factor|failure("factorTerm")) ^^ { case t~r => r match {case x: List[String~NumericFactor] => FactorTerm(t,x)}}
  def factor: Parser[NumericFactor] = (floatingPointNumber~opt("""[BMK]""".r) | "$"~identifier) ^^ { case s~Some(m) => Number(s,m.toString); case s~None => Number(s,"1"); case "$"~x => Identifier(x) }
}

