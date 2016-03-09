package com.marcos.sb.foo.db.akka.message

object Messages {
  case class SetRequest(key: String, value: Any)
  case class GetRequest(key: String)
  case class KeyNotFoundException(key: String) extends Exception
}
