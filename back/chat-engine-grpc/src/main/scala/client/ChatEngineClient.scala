package com.bravewave.conferencing
package client

import akka.actor.typed.ActorSystem
import akka.grpc.GrpcClientSettings
import com.bravewave.conferencing.chatgrpc.gen._
import com.bravewave.conferencing.conf.shared.ConferenceId

import scala.concurrent.Future

class ChatEngineClient(implicit actorSystem: ActorSystem[_]) {

  private val clientSettings = GrpcClientSettings.fromConfig(ChatEngineService.name)
  private val client = ChatEngineServiceClient(clientSettings)

  def spawnChat(conferenceId: ConferenceId): Future[SpawnChatResponse] = client.spawnChat(SpawnChatRequest(conferenceId))

  def sendMessage(in: ChatMessageRequest): Future[ChatMessageResponse] = client.sendMessage(in)
}
