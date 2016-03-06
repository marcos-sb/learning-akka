package com.marcos.sb.foo.db.akka.actor

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import com.marcos.sb.foo.db.akka.message.Messages.SetRequest
import org.scalatest.{BeforeAndAfterEach, FunSuite, Matchers}

class TestAkkaDBActor extends FunSuite
  with Matchers
  with BeforeAndAfterEach {

  implicit val system = ActorSystem()

  test("Place key/value into map") {
    val actorRef = TestActorRef(new AkkaDBActor)
    actorRef ! SetRequest("key", "value")
    val akkaDbActor = actorRef.underlyingActor

    akkaDbActor.map.get("key") should equal(Some("value"))
  }
}
