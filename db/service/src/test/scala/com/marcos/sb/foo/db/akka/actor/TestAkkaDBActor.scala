package com.marcos.sb.foo.db.akka.actor

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.util.Timeout
import akka.pattern.ask
import com.marcos.sb.foo.db.akka.message.Messages._
import org.scalatest.{BeforeAndAfterEach, FunSuite, Matchers}
import scala.concurrent.duration._
import scala.concurrent.Await

class TestAkkaDBActor extends FunSuite
  with Matchers
  with BeforeAndAfterEach {

  implicit val system = ActorSystem()
  implicit val timeout = Timeout(2.seconds)

  test("Place key/value into map") {
    val actorRef = TestActorRef(new AkkaDBActor)
    actorRef ! SetRequest("key", "value")
    val akkaDbActor = actorRef.underlyingActor

    akkaDbActor.map.get("key") should equal(Some("value"))
  }

  test("Fetch existing value from map") {
    val actorRef = TestActorRef(new AkkaDBActor)
    actorRef ! SetRequest("key", "value")

    val output = Await.result(actorRef ? GetRequest("key"), 10.seconds)
    output shouldEqual "value"
  }

  test("Fetch non-existing value from map") {
    val actorRef = TestActorRef(new AkkaDBActor)

    intercept[KeyNotFoundException] {
      Await.result(actorRef ? GetRequest("key"), 10.seconds)
    }
  }

  test("Set if not present") {
    val actorRef = TestActorRef(new AkkaDBActor)
    actorRef ! SetIfNotFound("key", "value")

    val uActor = actorRef.underlyingActor
    val output = uActor.map.get("key")

    output shouldEqual Some("value")
  }

  test("Don't set if present") {
    val actorRef = TestActorRef(new AkkaDBActor)
    actorRef ! SetRequest("key", "value")
    actorRef ! SetIfNotFound("key", "value2")

    val uActor = actorRef.underlyingActor
    val output = uActor.map.get("key")

    output shouldEqual Some("value")
  }

  test("Delete key not in map") {
    val actorRef = TestActorRef(new AkkaDBActor)
    actorRef ! Delete("key")

    val uActor = actorRef.underlyingActor
    val output = uActor.map.get("key")

    output shouldEqual None
  }

  test("Delete existing key from map") {
    val actorRef = TestActorRef(new AkkaDBActor)
    actorRef ! SetRequest("key", "value")
    actorRef ! Delete("key")

    val uActor = actorRef.underlyingActor
    val output = uActor.map.get("key")

    output shouldEqual None
  }
}
