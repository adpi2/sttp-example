package sttp3.examples

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import sttp.client3.*
import sttp.client3.okhttp.OkHttpSyncBackend
import sttp.client3.okhttp.OkHttpFutureBackend
import sttp.ws.WebSocket
import sttp.client3.logging.LoggingBackend
import sttp.client3.logging.scribe.ScribeLoggingBackend

// Requests

val request1 = basicRequest
val request2 = basicRequest.get(uri"...")
val request3 = basicRequest.get(uri"...").response(asStringAlways)
val request4 = basicRequest.response(asStringAlways).get(uri"...")

// Backends

val backend1 = HttpClientSyncBackend()
val backend2 = HttpClientFutureBackend()
val backend3 = OkHttpSyncBackend()
val backend4 = OkHttpFutureBackend()

// Responses

// val response1 = backend1.send(request1)
// val response2 = request1.send(backend1)
val response3 = backend1.send(request2)
val response4 = request2.send(backend1)
val response5 = backend2.send(request2)

// WebSocket Requests

def useWebSocket1(ws: WebSocket[Identity]): String =
  ws.sendText("Hello World")
  ws.receiveText()

def useWebSocket2(ws: WebSocket[Future]): Future[String] =
  implicit val ec = ExecutionContext.global
  for
    _ <- ws.sendText("Hello World")
    response <- ws.receiveText()
  yield response

// val wsRequest1 = basicRequest.get(uri"...").response(asWebSocketAlways(useWebSocket1))
val wsRequest2 = basicRequest.get(uri"...").response(asWebSocketAlways(useWebSocket2))

// WebSocket responses

// val wsResponse1 = backend1.send(wsRequest1)
// val wsResponse2 = wsRequest1.send(backend1)
// val wsResponse3 = backend2.send(wsRequest1)
// val wsResponse4 = wsRequest1.send(backend2)
// val wsResponse5 = backend3.send(wsRequest1)
// val wsResponse6 = wsRequest1.send(backend3)
val wsResponse7 = backend2.send(wsRequest2)
val wsResponse8 = wsRequest2.send(backend2)

// Wrapper Backends

val logBackend1 = ScribeLoggingBackend(backend1)
val logBackend2 = ScribeLoggingBackend(backend2)
val logBackend3 = ScribeLoggingBackend(backend3)
val logBackend4 = ScribeLoggingBackend(backend4)

// Generic customization of requests

def addCommonSettings[T, R](request: Request[T, R]): Request[T, R] =
  request.auth
    .basic("user", "password")
    .cookies("foo" -> "bar")
    .maxRedirects(2)

val request2Bis = addCommonSettings(request2)
val request3Bis = addCommonSettings(request3)
val wsRequest2Bis = addCommonSettings(wsRequest2)
