package com.marcos.sb.learn.db.akka

import com.marcos.sb.learn.db.akka.message.SetRequest
import org.scalatest.{FunSuite, BeforeAndAfterEach, Matchers}
import akka.actor.ActorSystem
import akka.testkit.TestActorRef

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
