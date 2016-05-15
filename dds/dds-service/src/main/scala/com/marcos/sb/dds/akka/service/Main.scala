package com.marcos.sb.dds.akka.service

import akka.actor.ActorSystem
import akka.actor.Props

object Main extends App {
  val system = ActorSystem("dds")
  val clusterController = system.actorOf(Props[ClusterController], "cc")
}
