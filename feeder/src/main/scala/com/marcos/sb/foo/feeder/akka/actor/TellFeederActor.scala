package com.marcos.sb.foo.feeder.akka.actor

import java.util.concurrent.TimeoutException

import akka.event.Logging
import akka.actor.Status.Failure
import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout
import scala.concurrent.duration._
import com.marcos.sb.foo.feeder.akka.message.Messages._
import com.marcos.sb.foo.db.akka.message.Messages._

final class TellFeederActor(
  cacheActorPath: String,
  httpClientActorPath: String,
  articleParserActorPath: String,
  implicit val timeout: Timeout
) extends Actor {

  private val cacheActor = context.actorSelection(cacheActorPath)
  private val httpClientActor = context.actorSelection(httpClientActorPath)
  private val articleParserActor = context.actorSelection(articleParserActorPath)
  private val logger = Logging(context.system, this)

  implicit val ec = context.dispatcher

  override def receive = {
    case ParseArticle(uri) =>
      val extraActor = buildExtraActor(sender(), uri)

      cacheActor.tell(GetRequest(uri),extraActor)
      httpClientActor.tell(GetRequest(uri),extraActor)

      context.system.scheduler.scheduleOnce(timeout.duration, extraActor, "timeout")
  }

  private def buildExtraActor(senderRef: ActorRef, uri: String) = {
    context.actorOf(Props(new Actor {

      override def receive = {
        case "timeout" =>
          senderRef ! Failure(new TimeoutException("timeout!"))
          context.stop(self)

        case HttpResponse(body) =>
          articleParserActor ! ParseHtmlArticle(uri, body)

        case body: String =>
          senderRef ! body
          context.stop(self)

        case ArticleBody(uri, body) =>
          cacheActor ! SetRequest(uri, body)
          senderRef ! body
          context.stop(self)

        case t =>
          logger.warning(s"ignoring msg type: ${t.getClass}")

      }

    }))
  }

}
