package com.marcos.sb.foo.feeder.akka.message

object Messages {
  case class ParseArticle(uri: String)
  case class ParseHtmlArticle(uri: String, html: String)
  case class HttpResponse(body: String)
  case class ArticleBody(uri: String, body: String)
}
