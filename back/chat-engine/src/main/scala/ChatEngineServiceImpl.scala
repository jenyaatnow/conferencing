package com.bravewave.conferencing.chat

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.util.Timeout
import com.bravewave.conferencing.chat.ChatEngineDispatcher.protocol._
import com.bravewave.conferencing.chatgrpc.gen.{ChatEngineService, ChatMessageRequest, ChatMessageResponse, SpawnChatRequest, SpawnChatResponse}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class ChatEngineServiceImpl(implicit system: ActorSystem[ChatEngineDispatcherMessage]) extends ChatEngineService {
  implicit private val timeout: Timeout = 5.seconds // todo move to conf

  override def spawnChat(in: SpawnChatRequest): Future[SpawnChatResponse] =
    system.ask { replyTo => SpawnConfChat(in, replyTo) }

  override def sendMessage(in: ChatMessageRequest): Future[ChatMessageResponse] =
    system.ask { replyTo => SendChatMessage(in, replyTo) }
}
