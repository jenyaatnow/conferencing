package com.bravewave.conferencing.conf

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{BinaryMessage, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.duration.DurationInt
import scala.io.StdIn


object WsSample extends App {
  private implicit val system = ActorSystem(Behaviors.empty, "my-system")
  private implicit val executionContext = system.executionContext

  val testSource = Source
    .repeat("Hello")
    .throttle(1, 1.seconds)
    .map(TextMessage(_))
    .limit(1000)

  val router =
    path("ws") {
      extractWebSocketUpgrade { upgrade =>
        complete(upgrade.handleMessagesWithSinkSource(
          Sink.foreach {
            case tm: TextMessage => tm.textStream.runForeach(print)
            case bm: BinaryMessage => bm.dataStream.runWith(Sink.ignore)
          },
          testSource,
        ))
      }
    }

  private val bindingFuture = Http().newServerAt("localhost", 8080).bind(router)

  println(s"Server now online. Please navigate to http://localhost:8080/sample/hello\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
