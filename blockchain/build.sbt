name := "blockchain"

version := "1.0"

scalaVersion := "2.11.8"

resolvers ++= Seq("RoundEights" at "http://maven.spikemark.net/roundeights")

resolvers ++= Seq("jBCrypt Repository" at "http://repo1.maven.org/maven2/org/")

libraryDependencies ++= Seq("com.roundeights" %% "hasher" % "1.2.0")

libraryDependencies ++= Seq("org.mindrot" % "jbcrypt" % "0.3m")