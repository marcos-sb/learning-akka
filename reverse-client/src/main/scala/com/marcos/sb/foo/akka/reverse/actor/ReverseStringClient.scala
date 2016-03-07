package com.marcos.sb.foo.akka.reverse.actor

import akka.util.Timeout
import akka.actor.ActorSystem
import akka.pattern.ask
import com.marcos.sb.foo.akka.reverse.actor.ReverseStringActor._

import scala.concurrent.duration._

final class ReverseStringClient(private val remoteAddr: String) {
  private implicit val timeout = Timeout (2.seconds)
  private val system = ActorSystem("client")
  private val remote = system.actorSelection(s"akka.tcp://reverse@$remoteAddr/user/endpoint1")

  def rev(s: String) = {
    remote ? RequestReverse(s)
  }
}
