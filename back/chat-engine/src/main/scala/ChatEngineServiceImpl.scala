package com.bravewave.conferencing.chat

import com.bravewave.conferencing.chatgrpc.gen.{ChatEngineService, SpawnChatRequest, SpawnChatResponse}

import scala.concurrent.Future

class ChatEngineServiceImpl extends ChatEngineService {

  override def spawnChat(in: SpawnChatRequest): Future[SpawnChatResponse] =
    Future.successful(SpawnChatResponse(s"chat_${in.conferenceId}"))
}
