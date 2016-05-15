package com.marcos.sb.foo.db.akka.client

import org.scalatest.{FunSpec, Matchers}
import akka.testkit.TestProbe
import akka.actor.{ActorSystem, Status}
import scala.concurrent.Await
import scala.concurrent.duration._
import com.marcos.sb.foo.db.akka.message.Messages._

final class TestClientIntegration extends FunSpec with Matchers {

  implicit val system = ActorSystem("test")

  describe("Client tests") {
    val dbService = TestProbe()
    val client = new Client(dbService.ref.path.toString,0)

    it("should set a value") {
      val res = client.set("123", 123)
      dbService.expectMsgType[SetRequest]
      dbService.reply(Status.Success)
      val output = Await.result(res, 10.seconds)

      output shouldEqual Status.Success
    }

    it("should not set a value") {
      intercept[IllegalStateException] {
        val res = new Client("xxx",0).set("123", 123)
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
        val res = new Client("xxxx",0).get("123")
        val output = Await.result(res, 10.seconds)
      }
    }

  }

  describe("Client buffer tests") {
    val dbService = TestProbe()
    val client = new Client(dbService.ref.path.toString,2)

    it("should set three values - buffer two") {
      val res1 = client.set("123", 123)
      dbService.expectNoMsg()

      val res2 = client.set("4", 4)
      dbService.expectNoMsg()

      val res3 = client.set("5", 5)
      dbService.expectMsgAllOf(SetRequest("123",123),SetRequest("4",4), SetRequest("5",5))
      dbService.reply(Status.Success)

      val output1 = Await.result(res1, 10.seconds)
      val output2 = Await.result(res2, 10.seconds)
      val output3 = Await.result(res3, 10.seconds)

      output1 shouldEqual Status.Success
      output2 shouldEqual Status.Success
      output3 shouldEqual Status.Success
    }
  }

  describe("Client connect test") {
    val dbService = TestProbe()
    val client = new Client(dbService.ref.path.toString,0)

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
