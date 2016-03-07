package com.marcos.sb.foo.akka.reverse.actor

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.pattern.ask
import org.scalatest.{FunSuite, Matchers}
import scala.concurrent.duration._
import scala.concurrent.Await
import akka.util.Timeout

import com.marcos.sb.foo.akka.reverse.actor.ReverseStringActor._

final class TestReverseStringActor extends FunSuite with Matchers {

  implicit val system = ActorSystem()
  implicit val timeout = Timeout (2.seconds)

  test("reverse - regular string") {
    val input = "foobar"
    val expected = "raboof"

    val actorRef = TestActorRef(new ReverseStringActor)
    val output =
      Await.result(actorRef ? RequestReverse(input), 10.seconds)

    output shouldEqual expected
  }

  test("reverse - int should throw IllegalArgumentException") {
    val input = 123
    val expected = 321

    val actorRef = TestActorRef(new ReverseStringActor)

    an [IllegalArgumentException] should be thrownBy
      Await.result((actorRef ? input), 10.seconds)
  }

}
