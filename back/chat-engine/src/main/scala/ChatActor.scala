package com.bravewave.conferencing.chat

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{Behavior, PostStop}
import cats.implicits.catsSyntaxOptionId
import com.bravewave.conferencing.chat.ChatActor.protocol.ChatActorProtocol
import com.bravewave.conferencing.chatgrpc.gen.SendMessageRes
import com.bravewave.conferencing.conf.shared.ChatId
import com.google.protobuf.timestamp.Timestamp

import java.time.Instant

object ChatActor {

  def apply(chatId: ChatId): Behavior[ChatActorProtocol] = Behaviors.setup { ctx =>
    ctx.log.info(s"Spawned new chat [id='$chatId']")
    ChatActor(State(chatId))
  }

  private def apply(state: State): Behavior[ChatActorProtocol] = Behaviors.receiveMessage[ChatActorProtocol] {
    case SendChatMessage(in, replyTo) =>
      val message = SendMessageRes(
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
  }.receiveSignal {
    case (ctx, PostStop) =>
      ctx.log.info(s"Chat [id='${state.chatId}'] is stopped")
      Behaviors.same
  }


  private final case class State(chatId: ChatId, messages: List[SendMessageRes] = Nil) {
    def +(msg: SendMessageRes): State = copy(messages = messages :+ msg)
  }

  object protocol {
    trait ChatActorProtocol
  }
}
