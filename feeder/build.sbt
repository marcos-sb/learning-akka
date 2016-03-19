name := """feeder"""

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.6"

// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"
libraryDependencies += "com.syncthemall" % "boilerpipe" % "1.2.2"
libraryDependencies += "com.marcos-sb" %% "akka-db-service" % "0.0.1-SNAPSHOT"

// Uncomment to use Akka
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.1"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.4.1"
