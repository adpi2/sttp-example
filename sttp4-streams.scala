package sttp4.examples.streams

import cats.effect.IO
import cats.effect.unsafe.implicits.*
import fs2.Stream
import sttp.capabilities.fs2.Fs2Streams
import sttp.capabilities.WebSockets
import sttp.client4._
import sttp.client4.logging.scribe.ScribeLoggingBackend
import sttp.client4.httpclient.cats.HttpClientCatsBackend
import sttp.client4.httpclient.fs2.HttpClientFs2Backend
import sttp.ws.WebSocket

// Requests

val request = basicRequest.get(uri"...").response(asStringAlways)

def useWebSocket(ws: WebSocket[IO]): IO[String] =
  ws.sendText("Hello World").flatMap(_ => ws.receiveText())
val wsRequest = basicRequest.get(uri"...").response(asWebSocketAlways(useWebSocket))

val stream = Stream.emits[IO, Byte]("Hello World".getBytes)
val streamRequest = basicRequest.post(uri"...").streamBody(Fs2Streams[IO])(stream)

val wsStreamRequest = basicRequest
  .post(uri"...")
  .streamBody(Fs2Streams[IO])(stream)
// .response(asWebSocketAlways(useWebSocket))

// Backends

val backend1 = HttpClientCatsBackend.stub[IO]
val backend2 = HttpClientFs2Backend.stub[IO]

// Responses

val response1 = request.send(backend1)
val response2 = request.send(backend1)

// val wsResponse1 = wsRequest.send(backend1)
val wsResponse2 = wsRequest.send(backend2)

// val streamResponse1 = streamRequest.send(backend1)
val streamRespsone2 = streamRequest.send(backend2)

// val wsStreamResponse1 = wsStreamRequest.send(backend1)
val wsStreamRespsone2 = wsStreamRequest.send(backend2)

// Delegate backend

val logBackend1 = ScribeLoggingBackend(backend1)
val logBackend2 = ScribeLoggingBackend(backend2)
