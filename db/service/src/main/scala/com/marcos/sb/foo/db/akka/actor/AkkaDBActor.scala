package com.marcos.sb.foo.db.akka.actor

import akka.actor.{Props, ActorSystem, Actor, Status}
import akka.event.Logging
import com.marcos.sb.foo.db.akka.exception.KeyNotFoundException
import com.marcos.sb.foo.db.akka.message.Messages._
import scala.collection.mutable

class AkkaDBActor extends Actor {
  val map = new mutable.HashMap[String, Any]
  val log = Logging(context.system, this)

  override def receive = {
    case SetRequest(key, value) =>
      log.info(s"received SetRequest - key: $key value: $value")
      map += (key -> value)
      sender() ! Status.Success

    case GetRequest(key) =>
      log.info(s"received GetRequest - key: $key")
      map.get(key) match {
        case Some(v) => sender() ! v
        case None => sender() ! Status.Failure(new KeyNotFoundException(key))
      }

    case o =>
      log.info(s"received unknown message: $o");
  }
}


object Main extends App {
  val system = ActorSystem("db")
  val actor = system.actorOf(Props[AkkaDBActor], name = "db-actor")
}
