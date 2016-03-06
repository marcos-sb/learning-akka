package com.marcos.sb.foo.db.akka.actor

import org.scalatest.{FunSuite, BeforeAndAfterEach, Matchers}
import akka.actor.ActorSystem
import akka.testkit.TestActorRef

final class TestLastStringActor extends FunSuite
  with Matchers
  with BeforeAndAfterEach {

  implicit val system = ActorSystem()

  test("initial `laststring` is None") {
    val actorRef = TestActorRef(new LastStringActor)
    val lsa = actorRef.underlyingActor

    lsa.lastString should equal(None)
  }

  test("`laststring` is some string") {
    val actorRef = TestActorRef(new LastStringActor)
    actorRef ! "some string"
    val lsa = actorRef.underlyingActor

    lsa.lastString should equal(Some("some string"))
  }

  test("`laststring is some string - two messages") {
    val actorRef = TestActorRef(new LastStringActor)
    actorRef ! "s1"; actorRef ! "s2"
    val lsa = actorRef.underlyingActor

    lsa.lastString should equal(Some("s2"))
  }
}
