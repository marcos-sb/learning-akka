package com.marcos.sb.foo.db.akka.client

import akka.actor._
import akka.util.Timeout
import akka.pattern.ask
import com.marcos.sb.foo.db.akka.message.Messages.{GetRequest, SetRequest}

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

    private var endpointActorRef: Option[ActorRef] = None
    private val identityId = 1

    override def preStart = {
      context.actorSelection(remoteAddr) ! Identify(identityId)
    }

    override def receive = {
      case ActorIdentity(`identityId`, Some(ref)) =>
        endpointActorRef = Some(ref)

      case ActorIdentity(`identityId`, None) =>
        endpointActorRef = None

      case ReqOnline =>
        sender() ! (if(endpointActorRef.nonEmpty) Online else Offline)

      case m @ SetRequest(key, value) =>
        if(endpointActorRef.isEmpty)
          sender() ! Status.Failure(new IllegalStateException("endpoint offline"))
        else endpointActorRef.foreach(_.forward(m))

      case m @ GetRequest(key) =>
        if(endpointActorRef.isEmpty)
          sender() ! Status.Failure(new IllegalStateException("endpoint offline"))
        else endpointActorRef.foreach(_.forward(m))
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

  def set(key: String, value: Any) = {
    clientActor ? SetRequest(key, value)
  }

  def get(key: String) = {
    clientActor ? GetRequest(key)
  }

}
