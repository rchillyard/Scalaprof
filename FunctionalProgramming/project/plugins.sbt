resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")
addSbtPlugin("com.typesafe.sbteclipse" %% "sbteclipse-plugin" % "4.0.0")
addSbtPlugin("com.eed3si9n" %% "sbt-assembly" % "0.13.0")
//addSbtPlugin("org.scoverage" % "scalac-scoverage-plugin_2.11" % "1.1.0")
//addSbtPlugin("com.typesafe" % "sbt-abide" % "0.1-SNAPSHOT")
