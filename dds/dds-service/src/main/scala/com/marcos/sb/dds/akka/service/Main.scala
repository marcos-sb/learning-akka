package com.marcos.sb.dds.akka.service

import akka.actor.ActorSystem
import akka.actor.Props
import akka.routing.BalancingPool
import akka.cluster.client.ClusterClientReceptionist


object Main extends App {
  val system = ActorSystem("dds")
  val clusterController = system.actorOf(Props[ClusterController], "cc")
  val workers = system.actorOf(BalancingPool(5).props(Props[ReverseString]), "workers")
  ClusterClientReceptionist(system).registerService(workers)
}
