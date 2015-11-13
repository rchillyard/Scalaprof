name := """SparkApp"""

version := "1.0"

scalaVersion := "2.10.4"

val spark = "org.apache.spark"
val sparkVersion = "1.5.1"

// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"
libraryDependencies += spark %% "spark-core" % sparkVersion % "provided"
libraryDependencies += spark %% "spark-mllib" % sparkVersion % "provided"

// Uncomment to use Akka
//libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.11"

