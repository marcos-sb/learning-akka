package com.marcos.sb.foo.db.akka.client

import akka.actor._
import akka.util.Timeout
import akka.pattern.ask
import akka.event.Logging
import com.marcos.sb.foo.db.akka.message.Messages.{GetRequest, SetRequest, Subscribe, HeartBeat}

import scala.concurrent.duration._

private object ClientPrivateMessages {
  case object ReqOnline
  case object Online
  case object Offline
}


final class Client(private val remoteAddr: String)(implicit val system: ActorSystem) {

  import ClientPrivateMessages._

  sealed class ClientActor(
    private val remoteAddr: String
  ) extends Actor {

    private val log = Logging(context.system,this)
    private var endpointActorRef: Option[ActorRef] = None
    private val identityId = 1

    override def preStart = {
      context.actorSelection(remoteAddr) ! Identify(identityId)
    }

    override def receive = {
      case ActorIdentity(`identityId`, Some(ref)) =>
        endpointActorRef = Some(ref)
        context.become(online)

      case ActorIdentity(`identityId`, None) =>
        endpointActorRef = None

      case ReqOnline =>
        sender() ! Offline

      case SetRequest(_, _) =>
        sender() ! Status.Failure(new IllegalStateException("endpoint offline"))

      case GetRequest(_) =>
        sender() ! Status.Failure(new IllegalStateException("endpoint offline"))

      case _ =>
    }

    def online: Receive = {
      case HeartBeat =>
        log.info(s"received HeartBeat from ${sender()}")

      case Subscribe =>
        endpointActorRef.foreach(_ ! Subscribe)

      case ActorIdentity(`identityId`, None) =>
        endpointActorRef = None
        context.unbecome()

      case ReqOnline =>
        sender() ! Online

      case m @ SetRequest(_,_) =>
        endpointActorRef.foreach(_.forward(m))

      case m @ GetRequest(_) =>
        endpointActorRef.foreach(_.forward(m))

      case _ =>
    }

  }

  private implicit val timeout = Timeout (5.seconds)
//  private val remoteDb = Client.system.actorSelection(s"akka.tcp://db@$remoteAddr/user/service")
  private val clientActor =
    system.actorOf(Props(classOf[ClientActor], this, remoteAddr))
  import scala.concurrent.ExecutionContext.Implicits.global

  def online = (clientActor ? ReqOnline) map {
    case Online => true
    case Offline => false
  }

  def subscribe = clientActor ! Subscribe

  def set(key: String, value: Any) = {
    clientActor ? SetRequest(key, value)
  }

  def get(key: String) = {
    clientActor ? GetRequest(key)
  }

}
