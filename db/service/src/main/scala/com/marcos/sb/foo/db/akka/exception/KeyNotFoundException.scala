package com.marcos.sb.foo.db.akka.exception

case class KeyNotFoundException(key: String) extends Exception
