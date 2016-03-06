package com.marcos.sb.foo.db.akka.actor

import akka.actor.Actor

final class LastStringActor extends Actor {
  private var lastString_ : Option[String] = None

  def lastString = lastString_

  override def receive = {
    case s: String => lastString_ = Some(s)
    case _ =>
  }
}
