package finalExam

import org.scalatest.{ FlatSpec, Matchers }

class DNASpec extends FlatSpec with Matchers {

  "DNA(CTG)" should "appear as CTG" in {
    val dna = DNA("CTG")
    dna.toString should be ("CTG")
  }
  it should "be CTGAG when combined with AG" in {
    val dna = DNA("CTG")++DNA("AG")
    dna.toString should be ("CTGAG")
  }
  it should "be list (reverse order) when zipped" in {
    val zip = DNA("CTG") zip DNA("AGC")
    zip should be (List((Guanine,Cytosine),(Thymine,Guanine),(Cytosine,Adenine)))
  }
  it should "distance 2 from AGG" in {
    val dist = DNA("CTG") euclidean DNA("AGG")
    dist should be (2)
  }
  it should "have 3 bases" in {
    val dna = DNA("CTG")
    dna.bases should be (3)
  }
}