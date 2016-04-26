package com.phasmid.bridge.duplicate

import com.phasmid.bridge.duplicate.Howell.{Trio, MovePlan, Moves}

import scala.annotation.tailrec
import scala.collection.generic.CanBuildFrom
import scala.collection.mutable

/**
 * @author robinhillyard
 */
/**
 * tables is the total number of tables, including phantom tables (should be an odd number).
 * For a complete movement, the number of pairs is the number of tables plus one.
 * The number of true (physical) tables is half the number of pairs.
 * The number of board sets is the same as the number of tables.
 * All positions are 0-based
 */
case class Howell(tables: Int, movePlan: MovePlan) {
  def moves(current: Position, movement: Movement): (Position,Movement) = {
    val head = movement.head
    val tail = movement.tail
    val result = for (e <- current.encounters) yield e.move(head, current)
    implicit val t = tables
    (Position(result),Movement(tail))
  }
  private def getMovement(implicit tables: Int): Movement = Movement(Triple.toStreams(movePlan))
  def positions(start: Position) = {
    @tailrec def loop(positions: List[Position], posMov: (Position,Movement), moves: Int): List[Position] = moves match {
      case 0 => positions
      case _ => loop(positions :+ posMov._1, this.moves(posMov._1, posMov._2), moves-1)
    }
    implicit val t = tables
    loop(List(), (start, getMovement), tables)
  }
}

case class Position(encounters: Seq[Encounter]) {
  override def toString = encounters mkString " "
}

abstract class MappedProduct3[T,F[_]](_1: T, _2: T, _3: T) extends Product3[T,T,T] {
  def map[U](f: T=>U)(implicit cbf: CanBuildFrom[F[U], U, F[U]]): F[U] = {
    val ts = productIterator
    val us = for (t <- ts) yield f(t.asInstanceOf[T])
    val x: mutable.Builder[U, F[U]] = cbf()
    for (u <- us) x += u
    x.result()
  }

  override def toString = s"n:${_1} e:${_2} b:${_3}"
}

case class Triple[T](_1: T, _2: T, _3: T) extends Product3[T,T,T] {
  def map[U](f: T=>U): Triple[U] = ??? // TODO
  override def toString = s"n:${_1} e:${_2} b:${_3}"
}

object Triple {
  def zip[U](ust: Triple[Stream[U]]): Stream[Triple[U]] = ??? // TODO
  def toStreams[U](ust: Triple[Seq[U]]): Triple[Stream[U]] = ??? // TODO
}

case class Movement(moves: Stream[Trio]) {
  def head = moves.head
  def tail = moves.tail
}

object Movement{
  def apply(x: Moves)(implicit tables: Int): Movement =
    Movement(Triple.zip(x))
}

object Howell extends App {
  type Trio = Triple[Int]
  type MovePlan = Triple[Seq[Int]]
  type Moves = Triple[Stream[Int]]

  def howell7: (Howell,Position) = {
    implicit val tables = 7
    val howell = Howell(tables, Triple(Seq(-3), Seq(-2), Seq(-1)))
    val start = Position(Seq(Encounter(1, 1, 1, 1), Encounter(2, 1, 1, 2), Encounter(3, 1, 2, 3), Encounter(4, 2, 1, 4), Encounter(5, 2, 1, 5), Encounter(6, 1, 1, 6), Encounter(7, 1, 1, 7)))
    (howell,start)
  }
  def howell9: (Howell,Position) = {
    implicit val tables = 9
    val howell = Howell(tables, Triple(Seq(-3, -3, -2), Seq(-2), Seq(-1)))
    val start = Position(Seq(Encounter(1, 1, 1, 1), Encounter(2, 1, 1, 2), Encounter(3, 1, 2, 3), Encounter(4, 2, 1, 4), Encounter(5, 2, 1, 5), Encounter(6, 1, 1, 6), Encounter(7, 1, 1, 7), Encounter(8, 1, 1, 8), Encounter(9, 1, 1, 9)))
    (howell,start)
  }

  val (h,s) = howell7
  for ((r,p) <- labelPositions(h.positions(s)))
    println(s"Round $r: $p")
  def labelPositions(positions: List[Position]) = for ((p,r) <- positions zip Stream.from(1)) yield (r,p)
}

case class Encounter(table: Int, n: Int, e: Int, b: Int)(implicit tables: Int) {
  def move(moves: Trio, current: Position): Encounter = {
    val x = moves map {i => current.encounters(modulo(table-i)-1)}
    Encounter.fromPrevious(table,x)
  }
  // Transforms a number n in the range 1-tables..infinity into the range 1..tables
  def modulo(n: Int): Int = ??? // TODO
  override def toString = s"T$table: $n-$e@#$b"
}
object Encounter {
  def fromPrevious(table: Int, et: Triple[Encounter])(implicit tables: Int): Encounter = apply(table, et._1.n,et._2.e,et._3.b)
}