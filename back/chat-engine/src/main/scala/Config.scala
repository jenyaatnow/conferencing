package com.bravewave.conferencing.chat

import com.typesafe.config.{Config => TypesafeConfig, ConfigFactory}

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

object Config {
  val conf: TypesafeConfig = ConfigFactory.parseResources("chat-engine.conf").resolve()

  val host: String = conf.getString("app.grpc-server.host")
  val port: Int = conf.getInt("app.grpc-server.port")

  val actorLookupTimeout: FiniteDuration =
    FiniteDuration(conf.getDuration("app.actor-lookup-timeout").toNanos, TimeUnit.NANOSECONDS)
  val chatEngineDispatcherAskTimeout: FiniteDuration =
    FiniteDuration(conf.getDuration("app.chat-engine-dispatcher-ask-timeout").toNanos, TimeUnit.NANOSECONDS)
}
