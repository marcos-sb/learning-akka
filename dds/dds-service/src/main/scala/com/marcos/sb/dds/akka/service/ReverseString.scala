package com.marcos.sb.dds.akka.service

import akka.actor.{Actor, Status}
import akka.event.Logging
import com.marcos.sb.dds.akka.message.Message._

final class ReverseStringActor extends Actor {
  val logger = Logging(context.system, this)

  override def receive = {
    case ReverseStringReq(s: String) =>
      sender() ! ReverseStringRes(s.reverse)
    case _ =>
      sender() ! Status.Failure(new IllegalArgumentException())
  }
}
