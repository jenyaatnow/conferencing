package com.bravewave.conferencing.conf

import akka.actor.typed.{ActorSystem, SpawnProtocol}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import com.bravewave.conferencing.conf.ws.ConferenceSessionMap
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

object ConfEngineServer extends App {

  private val conf = ConfigFactory.parseResources("conf-engine.conf")
  private implicit val actorSystem = ActorSystem(SpawnProtocol(), "conf-engine", conf)

  private def messageRoute =
    headerValueByName("user-id") { userId =>
      pathPrefix("conference" / Segment) { conferenceId =>
        // await on the webflow materialization pending session actor creation by the spawnSystem
        Await.ready(ConferenceSessionMap.findOrCreate(conferenceId).webflow(userId), Duration.Inf).value.get match {
          case Success(flow) => handleWebSocketMessages(flow)
          case Failure(exception) =>
            actorSystem.log.error(exception.getMessage)
            failWith(exception)
        }
      }
    }

  // todo move to conf
  private val host = "localhost"
  private val port = 8080
  Http().newServerAt(host, port).bind(messageRoute)
  actorSystem.log.info(s"Server started at $host:$port")
}
