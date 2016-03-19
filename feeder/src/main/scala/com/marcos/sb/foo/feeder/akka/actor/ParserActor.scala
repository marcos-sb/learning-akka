package com.marcos.sb.foo.feeder.akka.actor

import akka.actor.Actor
import akka.event.Logging
import com.marcos.sb.foo.feeder.akka.message.Messages._

final class ParserActor extends Actor {
  private val logger = Logging(context.system, this)
  override def receive = {
    case ParseHtmlArticle(key, html) =>
      sender() ! ArticleBody(key, de.l3s.boilerpipe.extractors.ArticleExtractor.INSTANCE.getText(html))
    case x =>
      logger.warning("unknown message $x")
  }
}
