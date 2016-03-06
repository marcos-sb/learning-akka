package com.marcos.sb.foo.db.akka.client

import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._

final class TestClientIntegration extends FunSuite with Matchers {

  test("set a value") {
    val client = new Client("127.0.0.1:2552")
    client.set("123", 123)

    val expected = 123
    val output = Await.result(client.get("123"), 10.seconds)

    output shouldEqual 123
  }

}
