package com.marcos.sb.foo.db.akka.client

import akka.actor._
import akka.util.Timeout
import akka.pattern.ask
import akka.event.Logging
import com.marcos.sb.foo.db.akka.message.Messages._

import scala.concurrent.duration._

private object ClientPrivateMessages {
  case object ReqOnline
  case object Online
  case object Offline
}


final class Client(
  private val remoteAddr: String,
  private val bunchSz: Int = 3)
  (implicit val system: ActorSystem) {

  import ClientPrivateMessages._

  sealed trait State
  case object Disconnected extends State
  case object Connected extends State

  sealed trait Data
  case object Empty extends Data
  sealed case class Pending(reqs: List[Request], backend: ActorRef) extends Data

  sealed class ClientActor(
    private val remoteAddr: String
  ) extends FSM[State, Data] {

    private val identityId = 1

    override def preStart() = {
      context.actorSelection(remoteAddr) ! Identify(identityId)
    }

    startWith(Disconnected, Empty)

    when(Disconnected) {
      case Event(ActorIdentity(`identityId`, Some(ref)), _) =>
        goto(Connected) using Pending(List.empty[Request], ref)

      case Event(ActorIdentity(`identityId`, None), _) =>
        stay

      case Event(req: Request, _) =>
        sender() ! Status.Failure(new IllegalStateException("endpoint offline"))
        stay

      case Event(ReqOnline, _) =>
        sender() ! Offline
        stay

      case _ =>
        log.error(s"received msg while $Disconnected")
        stay
    }

    when(Connected) {
      case Event(req: Request, Pending(l, ref)) =>
        if(l.size < bunchSz)
          stay using Pending(req :: l, ref) replying Status.Success
        else {
          for(req_ <- l.reverse) ref.forward(req_)
          ref.forward(req)
          stay using Pending(List.empty[Request], ref)
        }

      case Event(s @ Subscribe, p @ Pending(_, ref)) => // subscribe is not buffered but sent right away
        ref ! s
        stay

      case Event(HeartBeat, _) =>
        log.info(s"received heartbeat from ${sender()}")
        stay

      case Event(ReqOnline, _) =>
        sender() ! Online
        stay

      case _ =>
        log.error(s"received unknown msg while $Online")
        stay
    }

    initialize()

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

  def subscribe() = clientActor ! Subscribe

  def set(key: String, value: Any) = {
    clientActor ? SetRequest(key, value)
  }

  def get(key: String) = {
    clientActor ? GetRequest(key)
  }

}
