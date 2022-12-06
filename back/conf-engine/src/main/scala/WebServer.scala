package com.bravewave.conferencing.conf

import akka.actor.typed.{ActorSystem, SpawnProtocol}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Flow

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

object WebServer extends App {


  implicit val spawnSystem = ActorSystem(SpawnProtocol(), "spawn")
  implicit val executionContext = spawnSystem.executionContext

  def helloRoute: Route = pathEndOrSingleSlash {
    complete("Welcome to messaging service")
  }

  def affirmRoute = path("affirm") {
    handleWebSocketMessages(
      Flow[Message].collect {
        case TextMessage.Strict(text) => TextMessage("You said " + text)
      }
    )
  }

  def messageRoute =
    pathPrefix("message" / Segment) { trainerId =>
      // await on the webflow materialization pending session actor creation by the spawnSystem
      Await.ready(ChatSessionMap.findOrCreate(trainerId).webflow(), Duration.Inf).value.get match {
        case Success(value) => handleWebSocketMessages(value)
        case Failure(exception) =>
          println(exception.getMessage)
          failWith(exception)
      }
    }

  Http().newServerAt("localhost", 8080).bind(helloRoute ~ affirmRoute ~ messageRoute)
  println("Server running...")
}
