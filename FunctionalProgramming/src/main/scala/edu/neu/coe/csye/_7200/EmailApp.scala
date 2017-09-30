package edu.neu.coe.csye._7200

case class Mailer(server: String) {
  import java.net._
  val s = new Socket(InetAddress.getByName(server), 587)
  val out = new java.io.PrintStream(s.getOutputStream)
  def doMail(message: String, filename: String) = {
    val src = scala.io.Source.fromFile(filename)
    for (entry <- src.getLines.map(_.split(","))) out.println(s"To: ${entry(0)}\nDear ${entry(1)},\n$message")
    src.close
    out.flush()
  }  
  def close() = {
    out.close()
    s.close()
  }
}
object EmailApp {
  def main(args: Array[String]): Unit = {
    val mailer = Mailer("smtp.google.com")
    mailer.doMail(args(0), "mailinglist.csv")
    mailer.close()
  }
}