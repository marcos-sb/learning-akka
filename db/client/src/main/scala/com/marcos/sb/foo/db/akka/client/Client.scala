package com.marcos.sb.foo.db.akka.client

import akka.actor.ActorSystem
import akka.util.Timeout
import akka.pattern.ask
import com.marcos.sb.foo.db.akka.message.Messages.{Connect, Ping, GetRequest, SetRequest}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Success


final class Client(private val remoteAddr: String)(implicit val system: ActorSystem) {
  private implicit val timeout = Timeout (2.seconds)
//  private val remoteDb = Client.system.actorSelection(s"akka.tcp://db@$remoteAddr/user/service")
  private val remoteDb = system.actorSelection(remoteAddr)
  import scala.concurrent.ExecutionContext.Implicits.global
  private var _online = Future[Boolean] {false}

  def connect() = {
    _online = (remoteDb ? Ping) map {
      case Connect => true
      case _ => false
    }
    _online
  }

  def online = _online

  def set(key: String, value: Any) = {
    if(!_online.value.contains(Success(true))) throw new IllegalStateException("endpoint $remoteDb offline")
    remoteDb ? SetRequest(key, value)
  }

  def get(key: String) = {
    if(!_online.value.contains(Success(true))) throw new IllegalStateException("endpoint $remoteDb offline")
    remoteDb ? GetRequest(key)
  }

}
