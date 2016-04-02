package com.marcos.sb.foo.db.akka.actor

import akka.actor.{Props, ActorSystem, Actor, Status}
import akka.event.Logging
import com.marcos.sb.foo.db.akka.message.Messages._
import scala.collection.mutable

final class AkkaDBActor extends Actor {
  private val map_ = new mutable.HashMap[String, Any]
  private val log = Logging(context.system, this)

  def map = map_

  override def receive = {
    case Ping =>
      log.info(s"received ping")
      sender() ! Connect

    case SetRequest(key, value) =>
      log.info(s"received SetRequest - key: $key, value: $value")
      map += (key -> value)
      sender() ! Status.Success

    case GetRequest(key) =>
      log.info(s"received GetRequest - key: $key")
      map.get(key) match {
        case Some(v) => sender() ! v
        case None => sender() ! Status.Failure(new KeyNotFoundException(key))
      }

    case SetIfNotFound(key,value) =>
      log.info(s"received SetIfNotFound - key: $key, value: $value")
      if(!map_.contains(key)) map += (key -> value)
      sender() ! Status.Success

    case Delete(key) =>
      log.info(s"received Delete - key: $key")
      map_ -= key
      sender() ! Status.Success

    case o =>
      log.info(s"received unknown message: $o");
  }
}


object Main extends App {
  val system = ActorSystem("db")
  val actor = system.actorOf(Props[AkkaDBActor], name = "service")
}
