package com.marcos.sb.foo.db.akka.client

import org.scalatest.{FunSpec, Matchers}
import akka.testkit.TestProbe
import akka.actor.{Identify, ActorIdentity, ActorSystem, Status}
import scala.concurrent.Await
import scala.concurrent.duration._
import com.marcos.sb.foo.db.akka.message.Messages._

final class TestClientIntegration extends FunSpec with Matchers {

  implicit val system = ActorSystem("test")

  describe("Client tests") {
    val dbService = TestProbe()
    val client = new Client(dbService.ref.path.toString)

    it("should set a value") {
      val res = client.set("123", 123)
      dbService.expectMsgType[SetRequest]
      dbService.reply(Status.Success)
      val output = Await.result(res, 10.seconds)

      output shouldEqual Status.Success
    }

    it("should not set a value") {
      intercept[IllegalStateException] {
        val res = new Client("xxx").set("123", 123)
        val output = Await.result(res, 10.seconds)
      }
    }

    it("should get a value") {
      val res = client.get("123")
      dbService.expectMsgType[GetRequest]
      dbService.reply(123)
      val output = Await.result(res, 10.seconds)

      output shouldEqual 123
    }

    it("should not get a value") {
      intercept[IllegalStateException] {
        val res = new Client("xxxx").get("123")
        val output = Await.result(res, 10.seconds)
      }
    }

  }

  describe("Client connect test") {
    val dbService = TestProbe()
    val client = new Client(dbService.ref.path.toString)

    it("should stay offline - no response from endpoint") {
      val online = Await.result(new Client("xxx").online, 10.seconds)

      online shouldEqual false
    }

    it("should find an endpoint") {
      val online = Await.result(client.online, 10.seconds)

      online shouldEqual true
    }
  }
}
