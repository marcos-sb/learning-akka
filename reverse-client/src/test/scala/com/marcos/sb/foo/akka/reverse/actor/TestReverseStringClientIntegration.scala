package com.marcos.sb.foo.akka.reverse.actor

import org.scalatest.{FunSuite, Matchers}
import scala.concurrent.duration._
import scala.concurrent.Await

final class TestReverseStringClientIntegration extends FunSuite with Matchers {

  test("rev - regular string") {
    val input = "foobar"
    val expected = "raboof"

    val revClient = new ReverseStringClient("127.0.0.1:2660")
    val output = Await.result(revClient.rev(input), 10.seconds)

    output shouldEqual expected
  }

}
