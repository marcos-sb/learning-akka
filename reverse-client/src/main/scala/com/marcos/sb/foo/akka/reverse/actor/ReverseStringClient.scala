package com.marcos.sb.foo.akka.reverse.actor

import akka.util.Timeout
import akka.actor.ActorSystem
import akka.pattern.ask
import com.marcos.sb.foo.akka.reverse.actor.ReverseStringActor._

import scala.concurrent.duration._

object ReverseStringClient {
  private implicit val system = ActorSystem("client")
}

final class ReverseStringClient(private val remoteAddr: String) {
  private implicit val timeout = Timeout (2.seconds)
  private val remote = ReverseStringClient.system.actorSelection(s"akka.tcp://reverse@$remoteAddr/user/endpoint1")

  def rev(s: String) = {
    remote ? RequestReverse(s)
  }
}
