organization := "com.marcos-sb"
name := """dds-service"""

version := "0.0.1"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.1",
  "com.typesafe.akka" %% "akka-cluster" % "2.4.1",
  "com.typesafe.akka" %% "akka-contrib" % "2.4.1",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.1" % "test",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "com.marcos-sb" %% "dds-messages" % "0.0.1")
