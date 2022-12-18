package com.bravewave.conferencing

import akka.actor.typed.ActorRef
import com.bravewave.conferencing.chat.ChatActor.protocol.ChatActorProtocol
import com.bravewave.conferencing.chat.ChatEngineDispatcher.protocol.ChatEngineDispatcherProtocol
import com.bravewave.conferencing.chat.ConfChatsManager.protocol.ConfChatsManagerProtocol
import com.bravewave.conferencing.chatgrpc.gen.{SendMessageReq, SendMessageRes}

package object chat {

  final case class SendChatMessage(in: SendMessageReq, replyTo: ActorRef[SendMessageRes])
    extends ChatEngineDispatcherProtocol
      with ConfChatsManagerProtocol
      with ChatActorProtocol
}
