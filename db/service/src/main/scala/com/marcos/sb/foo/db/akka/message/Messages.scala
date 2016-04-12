package com.marcos.sb.foo.db.akka.message

object Messages {
  case object HeartBeat
  case object Subscribe
  case class SetRequest(key: String, value: Any)
  case class GetRequest(key: String)
  case class SetIfNotFound(key: String, value: Any)
  case class Delete(key: String)
  case class KeyNotFoundException(key: String) extends Exception
}
