package com.marcos.sb.foo.db.akka.client

import akka.actor.ActorSystem
import akka.util.Timeout
import akka.pattern.ask
import com.marcos.sb.foo.db.akka.message.Messages.{GetRequest, SetRequest}

import scala.concurrent.duration._

final class Client(private val remoteAddr: String) {
  private implicit val timeout = Timeout (2.seconds)
  private val system = ActorSystem("client")
  private val remoteDb = system.actorSelection(s"akka.tcp://db@$remoteAddr/user/service")

  def set(key: String, value: Any) = {
    remoteDb ? SetRequest(key, value)
  }

  def get(key: String) = {
    remoteDb ? GetRequest(key)
  }
}
