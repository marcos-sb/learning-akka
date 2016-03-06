name := "akka-db-service"

version := "0.0.1-SNAPSHOT"
organization := "com.marcos-sb"

scalaVersion := "2.11.7"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.1"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.4.1" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"

libraryDependencies += "com.typesafe.akka" %% "akka-remote" % "2.4.1"

mappings in (Compile, packageBin) ~= { _.filterNot { case (_, name) =>
  Seq("application.conf").contains(name)
}}
