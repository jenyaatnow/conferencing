package com.bravewave.conferencing.chat

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import cats.implicits.catsSyntaxOptionId
import com.bravewave.conferencing.chat.ChatActor.protocol.ChatActorMessage
import com.bravewave.conferencing.chatgrpc.gen.ChatMessageResponse
import com.google.protobuf.timestamp.Timestamp

import java.time.Instant

object ChatActor {

  def apply(state: ChatState = ChatState()): Behavior[ChatActorMessage] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case SendChatMessage(in, replyTo) =>
        val message = ChatMessageResponse(
          in.id,
          in.conferenceId,
          in.chatType,
          in.from,
          in.to,
          in.text,
          Timestamp(Instant.now()).some
        )
        replyTo ! message
        ChatActor(state + message)

      case _ => Behaviors.same
    }
  }

  object protocol {
    trait ChatActorMessage
  }
}

final case class ChatState(
  messages: List[ChatMessageResponse] = Nil,
) {
  def +(msg: ChatMessageResponse): ChatState = copy(messages :+ msg)
}
