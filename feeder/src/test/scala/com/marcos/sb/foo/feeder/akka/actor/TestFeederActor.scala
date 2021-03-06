package com.marcos.sb.foo.feeder.akka.actor

//inspiration
//https://github.com/jasongoodwin/learning-akka/blob/master/ch3/akkademaid-scala/src/test/scala/AskAndTellDemoSpec.scala

import akka.util.Timeout
import akka.actor.Status.Failure
import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import akka.pattern.ask
import org.scalatest.{FunSpec, Matchers}
import scala.concurrent.duration._
import scala.concurrent.Await
import com.marcos.sb.foo.feeder.akka.actor._
import com.marcos.sb.foo.feeder.akka.message.Messages._
import com.marcos.sb.foo.db.akka.message.Messages._
import com.marcos.sb.foo.feeder.akka.actor.{Articles => ArticleTests}

final class TestFeederActor extends FunSpec with Matchers {
  implicit val system = ActorSystem("test")
  implicit val timeout = Timeout(10 seconds)

  class HelperActors {

  }

  describe("ask tests") {
    val cacheProbe = TestProbe()
    val httpClientProbe = TestProbe()
    val articleParseActor = system.actorOf(Props[ParserActor])
    val askFeederActor = system.actorOf(Props(classOf[AskFeederActor],
      cacheProbe.ref.path.toString,
      httpClientProbe.ref.path.toString,
      articleParseActor.path.toString,
      timeout
    ))

    it("should provide parsed article") {
      val f = askFeederActor ? ParseArticle("http://www.google.com")

       //Cache gets the message first. Fail cache request.
       cacheProbe.expectMsgType[GetRequest]
       cacheProbe.reply(Failure(new Exception("no cache")))

       //if it fails, http client gets a request
       httpClientProbe.expectMsgType[GetRequest]
       httpClientProbe.reply(HttpResponse(ArticleTests.article1))

       cacheProbe.expectMsgType[SetRequest] //Article will be cached.

       val parsedArticle = Await.result(f, 10 seconds)
       parsedArticle.toString should include("I’ve been writing a lot in emacs lately")
       parsedArticle.toString should not include("<body>")
    }

    it("should provide cached article") {
      val f = askFeederActor ? ParseArticle("http://www.google.com")

      //Cache gets the message first. Fail cache request.
      cacheProbe.expectMsgType[GetRequest]
      cacheProbe.reply(de.l3s.boilerpipe.extractors.ArticleExtractor.INSTANCE.getText(ArticleTests.article1))

      val parsedArticle = Await.result(f, 10 seconds)
      parsedArticle.toString should include("I’ve been writing a lot in emacs lately")
      parsedArticle.toString should not include("<body>")
    }
  }


  describe("tell tests") {
    val cacheProbe = TestProbe()
    val httpClientProbe = TestProbe()
    val articleParseActor = system.actorOf(Props[ParserActor])
    val tellFeederActor = system.actorOf(Props(classOf[TellFeederActor],
      cacheProbe.ref.path.toString,
      httpClientProbe.ref.path.toString,
      articleParseActor.path.toString,
      timeout
    ))

    it("should provide parsed article") {
      val f = tellFeederActor ? ParseArticle("http://www.google.com")

       //Cache gets the message first. Fail cache request.
       cacheProbe.expectMsgType[GetRequest]
       cacheProbe.reply(Failure(new Exception("no cache")))

       //if it fails, http client gets a request
       httpClientProbe.expectMsgType[GetRequest]
       httpClientProbe.reply(HttpResponse(ArticleTests.article1))

       cacheProbe.expectMsgType[SetRequest] //Article will be cached.

       val parsedArticle = Await.result(f, 10 seconds)
       parsedArticle.toString should include("I’ve been writing a lot in emacs lately")
       parsedArticle.toString should not include("<body>")
    }

    it("should provide cached article") {
      val f = tellFeederActor ? ParseArticle("http://www.google.com")

      //Cache gets the message first. Fail cache request.
      cacheProbe.expectMsgType[GetRequest]
      cacheProbe.reply(de.l3s.boilerpipe.extractors.ArticleExtractor.INSTANCE.getText(ArticleTests.article1))

      val parsedArticle = Await.result(f, 10 seconds)
      parsedArticle.toString should include("I’ve been writing a lot in emacs lately")
      parsedArticle.toString should not include("<body>")
    }
  }


  describe("pipe tests") {
    val cacheProbe = TestProbe()
    val httpClientProbe = TestProbe()
    val articleParseActor = system.actorOf(Props[ParserActor])
    val tellFeederActor = system.actorOf(Props(classOf[TellFeederActor],
      cacheProbe.ref.path.toString,
      httpClientProbe.ref.path.toString,
      articleParseActor.path.toString,
      timeout
    ))

    it("should provide parsed article") {
      val f = tellFeederActor ? ParseArticle("http://www.google.com")

       //Cache gets the message first. Fail cache request.
       cacheProbe.expectMsgType[GetRequest]
       cacheProbe.reply(Failure(new Exception("no cache")))

       //if it fails, http client gets a request
       httpClientProbe.expectMsgType[GetRequest]
       httpClientProbe.reply(HttpResponse(ArticleTests.article1))

       cacheProbe.expectMsgType[SetRequest] //Article will be cached.

       val parsedArticle = Await.result(f, 10 seconds)
       parsedArticle.toString should include("I’ve been writing a lot in emacs lately")
       parsedArticle.toString should not include("<body>")
    }

    it("should provide cached article") {
      val f = tellFeederActor ? ParseArticle("http://www.google.com")

      //Cache gets the message first. Fail cache request.
      cacheProbe.expectMsgType[GetRequest]
      cacheProbe.reply(de.l3s.boilerpipe.extractors.ArticleExtractor.INSTANCE.getText(ArticleTests.article1))

      val parsedArticle = Await.result(f, 10 seconds)
      parsedArticle.toString should include("I’ve been writing a lot in emacs lately")
      parsedArticle.toString should not include("<body>")
    }
  }
}
