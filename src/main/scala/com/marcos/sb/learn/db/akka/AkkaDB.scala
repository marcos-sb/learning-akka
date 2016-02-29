package com.marcos.sb.learn.db.akka

import akka.actor.Actor
import akka.event.Logging
import com.marcos.sb.learn.db.akka.message.SetRequest
import scala.collection.mutable

class AkkaDB extends Actor {
  val map = new mutable.HashMap[String, Any]
  val log = Logging(context.system, this)

  override def receive = {
    case SetRequest(key, value) =>
      log.info(s"received SetRequest - key: $key value: $value")
      map += (key -> value)

    case o =>
      log.info(s"received unknown message: $o");
  }
}
