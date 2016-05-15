package com.marcos.sb.dds.akka.message

object Message {
  case class ReverseStringReq(s:String)
  case class ReverseStringRes(s:String)
}
