package com.marcos.sb.foo.db.akka.client

import org.scalatest.{FunSpec, Matchers}
import akka.testkit.TestProbe
import akka.actor.{ActorSystem, Status}
import scala.concurrent.Await
import scala.concurrent.duration._
import com.marcos.sb.foo.db.akka.message.Messages._

final class TestClientIntegration extends FunSpec with Matchers {

  implicit val system = ActorSystem("test")

  describe("Client offline tests") {
    val dbService = TestProbe()
    val client = new Client(dbService.ref.path.toString)

    it("should produce IllegalStateException on set - no connection") {
      intercept[IllegalStateException] {
        client.set("123", 123)
      }
    }

    it("should produce IllegalStateException on get - no connection") {
      intercept[IllegalStateException] {
        client.get("123")
      }
    }
  }

  describe("Client connect test") {
    val dbService = TestProbe()
    val client = new Client(dbService.ref.path.toString)

    it("should not establish a connection - stay offline") {
      val output = client.connect()

      dbService.expectMsgType[Ping.type]
      dbService.reply("elgoog")

      val online = Await.result(output, 10.seconds)

      online shouldEqual false
    }

    it("should establish a connection - become online") {
      val output = client.connect()

      dbService.expectMsgType[Ping.type]
      dbService.reply(Connect)

      val online = Await.result(output, 10.seconds)

      online shouldEqual true
    }
  }

  describe("Client method test") {
    val dbService = TestProbe()
    val client = new Client(dbService.ref.path.toString)

    val online = client.connect()
    dbService.expectMsgType[Ping.type]
    dbService.reply(Connect)
    Await.result(online, 10.seconds) //ensure online status

    it("Should set/get a value") {
      client.set("123", 123)
      dbService.expectMsgType[SetRequest]
      dbService.reply(Status.Success)

      val outputF = client.get("123")

      dbService.expectMsgType[GetRequest]
      dbService.reply(123)

      val expected = 123
      val output = Await.result(outputF, 10.seconds)

      output shouldEqual expected
    }
  }
}
