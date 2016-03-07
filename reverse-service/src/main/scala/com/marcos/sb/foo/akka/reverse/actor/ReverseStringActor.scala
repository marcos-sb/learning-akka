package com.marcos.sb.foo.akka.reverse.actor

import akka.actor.{ActorSystem, Actor, Props, Status}
import akka.event.Logging

object ReverseStringActor {
  case class RequestReverse(s: String)
}

final class ReverseStringActor extends Actor {
  val logger = Logging(context.system, this)

  override def receive = {
    case ReverseStringActor.RequestReverse(s: String) =>
      sender() ! s.reverse
    case _ =>
      sender() ! Status.Failure(new IllegalArgumentException())
  }
}

object Main extends App {
  val actorSystem = ActorSystem("reverse")
  val endpoint = actorSystem.actorOf(Props[ReverseStringActor], name = "endpoint1")
}
