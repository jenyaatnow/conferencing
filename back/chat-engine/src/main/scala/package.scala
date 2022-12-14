package com.bravewave.conferencing

import akka.actor.typed.ActorRef
import com.bravewave.conferencing.chat.ChatActor.protocol.ChatActorMessage
import com.bravewave.conferencing.chat.ChatEngineDispatcher.protocol.ChatEngineDispatcherMessage
import com.bravewave.conferencing.chatgrpc.gen.{ChatMessageRequest, ChatMessageResponse}

package object chat {

  final case class SendChatMessage(in: ChatMessageRequest, replyTo: ActorRef[ChatMessageResponse])
    extends ChatEngineDispatcherMessage
      with ChatActorMessage
}
