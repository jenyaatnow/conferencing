package com.bravewave.conferencing.chat

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import com.bravewave.conferencing.chat.ChatEngineDispatcher.protocol.ChatEngineDispatcherMessage
import com.bravewave.conferencing.chatgrpc.gen.ChatEngineServiceHandler
import com.typesafe.config.ConfigFactory

object ChatEngineServer extends App {

  private val conf = ConfigFactory.parseResources("chat-engine.conf")
  implicit private val actorSystem = ActorSystem[ChatEngineDispatcherMessage](ChatEngineDispatcher(), "chat-engine", conf)
  private val service = ChatEngineServiceHandler(new ChatEngineServiceImpl)

  // todo move to conf
  private val host = "localhost"
  private val port = 8090
  Http().newServerAt(host, port).bind(service)
  actorSystem.log.info(s"Server started at $host:$port")
}
