package com.marcos.sb.foo.feeder.akka.actor

import akka.actor.Actor
import akka.util.Timeout
import akka.event.Logging
import akka.pattern.pipe
import akka.pattern.ask
import com.marcos.sb.foo.db.akka.message.Messages._
import com.marcos.sb.foo.feeder.akka.message.Messages._
import scala.concurrent.Future
import scala.util.{Success, Failure}

final class PipeFeederActor(
  cacheActorPath: String,
  httpClientActorPath: String,
  articleParserActorPath: String,
  implicit val timeout: Timeout
) extends Actor {
  private val logger = Logging(context.system, this)

  private val cacheActor = context.actorSelection(cacheActorPath)
  private val httpClientActor = context.actorSelection(httpClientActorPath)
  private val articleParserActor = context.actorSelection(articleParserActorPath)

  import scala.concurrent.ExecutionContext.Implicits.global

  override def receive = {
    case ParseArticle(uri) =>

      val cacheResult = cacheActor ? GetRequest(uri)
      val result = cacheResult.recoverWith {
        case _ : Exception =>
          val fRawResult = httpClientActor ? GetRequest(uri)

          fRawResult flatMap {
            case HttpResponse(rawArticle) =>
              articleParserActor ? ParseHtmlArticle(uri, rawArticle)
            case x =>
              Future.failed(new Exception("unknown response"))
          }
      }

      result andThen {
        case Success(x: String) =>
          logger.info("cached result!")
          x
        case Success(x: ArticleBody) =>
        logger.info("new cached result!")
          cacheActor ! SetRequest(uri, x.body)
          x.body
        case Failure(t) =>
          logger.error("failure caused by $t")
          akka.actor.Status.Failure(t)
        case x =>
          logger.warning("unknown message $x")
          x

      } pipeTo sender()
  }
}
