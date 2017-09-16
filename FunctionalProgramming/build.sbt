name := "functionalProgramming"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.12.3"

val scalaTestVersion = "3.0.1"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
	"joda-time" % "joda-time" % "2.9.2",
	"org.scala-lang.modules" %% "scala-xml" % "1.0.6",
	"org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.6",
	"org.scalatest" %% "scalatest" % scalaTestVersion % "test",
	"org.ccil.cowan.tagsoup" % "tagsoup" % "1.2.1"
)

val sprayGroup = "io.spray"
val sprayJsonVersion = "1.3.2"
libraryDependencies ++= List("spray-json") map {c => sprayGroup %% c % sprayJsonVersion}
