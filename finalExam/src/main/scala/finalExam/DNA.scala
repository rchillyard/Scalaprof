package finalExam

/*
 * Class to represent a DNA molecule. The list (bs) will appear to be in reverse order
 * from the order in which molecule was built up.
 */
case class DNA(bs: List[Base]) {
  // add a base to the beginning of the list bs
  def +(b: Base): DNA = ??? // 6
  // concatenate two DNA molecules together
  def ++(d: DNA): DNA = ??? // 5
  // generate a list of base pairs (order doesn't matter)
  def zip(d: DNA): List[(Base,Base)] = ??? // 7
  // get the (total) Euclidean distance between this and "d" by using the DNA.dist method
  def euclidean(d: DNA): Int = ??? // 9
  // count the total number of bases 
  def bases: Int = ??? // 3
  // create a string from the toString methods of the various Bases.
  override def toString: String = ??? // 8
}
trait Base {
  def pair: Base
}
case object Cytosine extends Base {
  def pair = Guanine
  override def toString = "C"
}
case object Guanine extends Base {
  def pair = Cytosine
  override def toString = "G"
}
case object Adenine extends Base {
  def pair = Thymine
  override def toString = "A"
}
case object Thymine extends Base {
  def pair = Adenine
  override def toString = "T"
}
case class Invalid(x: Char) extends Base {
  def pair = null
  override def toString = s"<Invalid: $x>"
}
object Base {
  def apply(x: Char) = x match {
    case 'G' => Guanine
    case 'C' => Cytosine
    case 'A' => Adenine
    case 'T' => Thymine
    case _ => Invalid(x)
  }
}

object DNA {
  def apply(): DNA = apply(List())
  def apply(s: String): DNA = s.toSeq.foldLeft(DNA())({_+Base(_)})
  def dist(a: Base, b: Base) = if (a==b) 0 else 1
}
