package com.marcos.sb.dds.akka.client

import akka.actor.{ActorPath, ActorSystem}
import akka.cluster.client.{ClusterClientSettings, ClusterClient}
import akka.pattern.Patterns
import akka.util.Timeout
import com.marcos.sb.dds.akka.message.Message.{ReverseStringRes, ReverseStringReq}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App {
  val timeout = Timeout(Duration(5, "seconds"))
  val system = ActorSystem("ddsclient")
  val initialContacts = Set(ActorPath.fromString("akka.tcp://dds@127.0.0.1:2552/system/receptionist"))
  val receptionist = system.actorOf(ClusterClient.props(ClusterClientSettings(system).withInitialContacts(initialContacts)), "client")
  val msg = ClusterClient.Send("/user/workers", ReverseStringReq("test"), localAffinity=true)
  val f = Patterns.ask(receptionist, msg, timeout)
  val result = Await.result(f, timeout.duration).asInstanceOf[ReverseStringRes]
  println(s"result: ${result.s}")
}
