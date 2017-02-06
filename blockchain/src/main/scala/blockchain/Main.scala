package blockchain

import com.roundeights.hasher.Implicits._
import com.roundeights.hasher.{Hasher, Algo}
import scala.language.postfixOps

object Main extends App {

  def hashUsing ( algo: Algo, message: String ) = {
    val hash = algo( message )
    println( "Hashed using " + algo.name + ": " + hash.hex )
    println( "Matches: " + (algo(message) hash= hash) )
  }

  val hashMe = "The quick brown fox jumps over the lazy dog"

  hashUsing(Algo.sha256, hashMe)
  hashUsing(Algo.sha256, hashMe+".")
  hashUsing(Algo.sha256, "")

//  // Generate a few hashes
//  val md5 = hashMe.md5
//  val sha1 = hashMe.sha1
//  val bcrypt = hashMe.bcrypt
//
//  // Print each hex encoded hash
//  println( "MD5: " + md5.hex )
//  println( "SHA1: " + sha1.hex )
//  println( "BCrypt: " + bcrypt.hex )
//
//  // Compare the original value to each hashed value
//  // and print the boolean result
//  println("MD5 Matches: " + (hashMe.md5 hash= md5) )
//  println("SHA1 Matches: " + (hashMe.sha1 hash= sha1) )
//  println("BCrypt Matches: " + (hashMe.bcrypt hash= bcrypt) )
}
