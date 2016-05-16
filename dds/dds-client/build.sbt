organization := "com.marcos-sb"
name := "dds-client"

version := "0.0.1"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.1",
  "com.typesafe.akka" %% "akka-contrib" % "2.4.1",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.1" % "test",
  "com.marcos-sb" %% "dds-messages" % "0.0.1",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test")
