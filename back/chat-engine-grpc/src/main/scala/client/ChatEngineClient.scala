package com.bravewave.conferencing
package client

import akka.actor.typed.ActorSystem
import akka.grpc.GrpcClientSettings
import com.bravewave.conferencing.chatgrpc.gen._

import scala.concurrent.Future

class ChatEngineClient(implicit actorSystem: ActorSystem[_]) {

  private val clientSettings = GrpcClientSettings.fromConfig(ChatEngineService.name)
  private val client = ChatEngineServiceClient(clientSettings)

  def sendMessage(in: SendMessageReq): Future[ChatMessageRes] = client.sendMessage(in)
  def getChatMessages(in: GetChatMessagesReq): Future[GetChatMessagesRes] = client.getChatMessages(in)
  def killConfChats(in: KillConfChatsReq): Future[KillConfChatsRes] = client.killConfChats(in)
}
