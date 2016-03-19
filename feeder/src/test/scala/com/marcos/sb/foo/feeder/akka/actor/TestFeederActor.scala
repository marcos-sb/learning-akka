package com.marcos.sb.foo.feeder.akka.actor

//inspiration
//https://github.com/jasongoodwin/learning-akka/blob/master/ch3/akkademaid-scala/src/test/scala/AskAndTellDemoSpec.scala

import akka.util.Timeout
import akka.actor.Status.Failure
import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import akka.pattern.ask
import org.scalatest.{FunSuite, Matchers}
import scala.concurrent.duration._
import scala.concurrent.Await
import com.marcos.sb.foo.feeder.akka.actor._
import com.marcos.sb.foo.feeder.akka.message.Messages._
import com.marcos.sb.foo.db.akka.message.Messages._
import com.marcos.sb.foo.feeder.akka.actor.{Articles => ArticleTests}

final class TestFeederActor extends FunSuite with Matchers {
  implicit val system = ActorSystem("test")
  implicit val timeout = Timeout(10 seconds)

  class HelperActors {
    val cacheProbe = TestProbe()
    val httpClientProbe = TestProbe()
    val articleParseActor = system.actorOf(Props[ParserActor])
  }

  val helpers = new HelperActors()
  val askFeederActor = system.actorOf(Props(classOf[AskFeederActor],
    helpers.cacheProbe.ref.path.toString,
    helpers.httpClientProbe.ref.path.toString,
    helpers.articleParseActor.path.toString,
    timeout
  ))

  val helpers2 = new HelperActors()
  lazy val tellFeederActor = system.actorOf(Props(classOf[AskFeederActor],
    helpers2.cacheProbe.ref.path.toString,
    helpers2.httpClientProbe.ref.path.toString,
    helpers2.articleParseActor.path.toString,
    timeout
  ))

  test("provide parsed article") {
    val f = askFeederActor ? ParseArticle("http://www.google.com")

     //Cache gets the message first. Fail cache request.
     helpers.cacheProbe.expectMsgType[GetRequest]
     helpers.cacheProbe.reply(Failure(new Exception("no cache")))

     //if it fails, http client gets a request
     helpers.httpClientProbe.expectMsgType[GetRequest]
     helpers.httpClientProbe.reply(HttpResponse(ArticleTests.article1))

     helpers.cacheProbe.expectMsgType[SetRequest] //Article will be cached.

     val parsedArticle = Await.result(f, 10 seconds)
     parsedArticle.toString should include("I’ve been writing a lot in emacs lately")
     parsedArticle.toString should not include("<body>")
  }

  test("provide cached article") {
    val f = askFeederActor ? ParseArticle("http://www.google.com")

    //Cache gets the message first. Fail cache request.
    helpers.cacheProbe.expectMsgType[GetRequest]
    helpers.cacheProbe.reply(de.l3s.boilerpipe.extractors.ArticleExtractor.INSTANCE.getText(ArticleTests.article1))

    val parsedArticle = Await.result(f, 10 seconds)
    parsedArticle.toString should include("I’ve been writing a lot in emacs lately")
    parsedArticle.toString should not include("<body>")
  }
}
