package com.bravewave.conferencing.chat

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{Behavior, PostStop}
import cats.implicits.catsSyntaxOptionId
import com.bravewave.conferencing.chat.ChatActor.protocol.ChatActorProtocol
import com.bravewave.conferencing.chatgrpc.gen.{ChatMessageRes, GetChatMessagesRes}
import com.bravewave.conferencing.conf.shared.ChatId
import com.google.protobuf.timestamp.Timestamp

import java.time.Instant

object ChatActor {
  def name(chatId: ChatId) = s"chat@$chatId"
  private def logPrefix(chatId: ChatId) = s"[${name(chatId)}]"

  def apply(chatId: ChatId): Behavior[ChatActorProtocol] = Behaviors.setup { ctx =>
    ctx.log.info(s"${logPrefix(chatId)} Start")
    ChatActor(State(chatId))
  }

  private def apply(state: State): Behavior[ChatActorProtocol] = Behaviors.receiveMessage[ChatActorProtocol] {
    case SendChatMessage(in, replyTo) =>
      val message = ChatMessageRes(
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

    case GetChatMessages(_, replyTo) =>
      replyTo ! GetChatMessagesRes(state.messages)
      Behaviors.same

    case _ => Behaviors.same
  }.receiveSignal {
    case (ctx, PostStop) =>
      ctx.log.info(s"${logPrefix(state.chatId)} Finish")
      Behaviors.same
  }


  private final case class State(chatId: ChatId, messages: List[ChatMessageRes] = Nil) {
    def +(msg: ChatMessageRes): State = copy(messages = messages :+ msg)
  }

  object protocol {
    trait ChatActorProtocol
  }
}
