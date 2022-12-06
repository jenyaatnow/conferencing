package com.bravewave.conferencing.conf

import akka.actor.typed.{ActorSystem, SpawnProtocol}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import com.bravewave.conferencing.conf.ws.ConferenceSessionMap

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

object WebServer extends App {

  private implicit val spawnSystem = ActorSystem(SpawnProtocol(), "spawn")

  private def messageRoute =
    headerValueByName("user-id") { userId =>
      pathPrefix("conference" / Segment) { conferenceId =>
        // await on the webflow materialization pending session actor creation by the spawnSystem
        Await.ready(ConferenceSessionMap.findOrCreate(conferenceId).webflow(userId), Duration.Inf).value.get match {
          case Success(flow) => handleWebSocketMessages(flow)
          case Failure(exception) =>
            spawnSystem.log.error(exception.getMessage)
            failWith(exception)
        }
      }
    }

  // todo move to conf
  private val host = "localhost"
  private val port = 8080
  Http().newServerAt(host, port).bind(messageRoute)
  spawnSystem.log.info(s"Server started at $host:$port")
}
