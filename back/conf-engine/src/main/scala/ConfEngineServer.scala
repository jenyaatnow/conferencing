package com.bravewave.conferencing.conf

import akka.actor.typed.{ActorSystem, SpawnProtocol}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import com.bravewave.conferencing.conf.ws.ConferenceSessionMap

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

object ConfEngineServer extends App with CorsHandler {

  private implicit val actorSystem: ActorSystem[SpawnProtocol.Command] = ActorSystem(SpawnProtocol(), "conf-engine", Config.conf)

  private def router =
    pathPrefix("conference" / Segment) { conferenceId =>
      parameter("userId") { userId =>
        // await on the webflow materialization pending session actor creation by the spawnSystem
        Await.ready(ConferenceSessionMap.findOrCreate(conferenceId).webflow(userId), Duration.Inf).value.get match {
          case Success(flow) => handleWebSocketMessages(flow)
          case Failure(exception) =>
            actorSystem.log.error(exception.getMessage)
            failWith(exception)
        }
      }
    }

  Http().newServerAt(Config.host, Config.port).bind(cors { router })
  actorSystem.log.info(s"Server started at ${Config.host}:${Config.port}")
}
