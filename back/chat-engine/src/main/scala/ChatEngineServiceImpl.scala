package com.bravewave.conferencing.chat

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.util.Timeout
import com.bravewave.conferencing.chat.ChatEngineDispatcher.protocol.{ChatEngineDispatcherProtocol, KillConfChats}
import com.bravewave.conferencing.chatgrpc.gen._

import scala.concurrent.Future

class ChatEngineServiceImpl(implicit system: ActorSystem[ChatEngineDispatcherProtocol]) extends ChatEngineService {
  implicit private val timeout: Timeout = Config.chatEngineDispatcherAskTimeout

  override def sendMessage(in: SendMessageReq): Future[ChatMessageRes] =
    system.ask { replyTo => SendChatMessage(in, replyTo) }

  override def getChatMessages(in: GetChatMessagesReq): Future[GetChatMessagesRes] =
    system.ask { replyTo => GetChatMessages(in, replyTo) }

  override def killConfChats(in: KillConfChatsReq): Future[KillConfChatsRes] =
    system.ask { replyTo => KillConfChats(in.conferenceId, replyTo) }
}
