package com.bravewave.conferencing.chat

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import com.bravewave.conferencing.chat.ChatEngineDispatcher.protocol.ChatEngineDispatcherProtocol
import com.bravewave.conferencing.chat.Config._
import com.bravewave.conferencing.chatgrpc.gen.ChatEngineServiceHandler

object ChatEngineServer extends App {

  implicit private val actorSystem: ActorSystem[ChatEngineDispatcherProtocol] =
    ActorSystem(ChatEngineDispatcher.newInstance, "chat-engine", conf)
  private val service = ChatEngineServiceHandler(new ChatEngineServiceImpl)

  Http().newServerAt(host, port).bind(service)
  actorSystem.log.info(s"Server started at $host:$port")
}
