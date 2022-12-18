package com.bravewave.conferencing.chat

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import cats.implicits.{catsSyntaxOptionId, none}
import com.bravewave.conferencing.chat.ChatActor.protocol.ChatActorProtocol
import com.bravewave.conferencing.chat.ConfChatsManager.protocol.ConfChatsManagerProtocol
import com.bravewave.conferencing.chatgrpc.gen.{GetChatMessagesReq, GetChatMessagesRes, SendMessageReq}
import com.bravewave.conferencing.conf.shared.{ChatId, ChatTypes, ConferenceId, UserId}

object ConfChatsManager {

  def newInstance: Behavior[ConfChatsManagerProtocol] = ConfChatsManager()

  private def apply(state: State = State()): Behavior[ConfChatsManagerProtocol] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case message @ SendChatMessage(in, _) =>
        resolveChatId(in)
          .map { chatId =>

            val chatRef = state.chats.getOrElse(chatId, spawnChat(ctx, chatId))
            chatRef ! message
            ConfChatsManager(state + (chatId, chatRef))
          }.getOrElse(Behaviors.same)

      case message @ GetChatMessages(in, replyTo) =>
        resolveChatId(in).flatMap(state.chats.get).map(_ ! message).getOrElse(replyTo ! GetChatMessagesRes())
        Behaviors.same

      case _ => Behaviors.same
    }
  }

  private def spawnChat(ctx: ActorContext[ConfChatsManagerProtocol], chatId: String): ActorRef[ChatActorProtocol] =
    ctx.spawn(ChatActor(chatId), s"chat-$chatId")

  private def resolveChatId(conferenceId: ConferenceId): String = s"${ChatTypes.conf}@$conferenceId"

  private def resolveChatId(msg: GetChatMessagesReq): Option[ChatId] =
    resolveChatId((msg.conferenceId, msg.chatType, msg.from, msg.to))

  private def resolveChatId(msg: SendMessageReq): Option[ChatId] =
    resolveChatId((msg.conferenceId, msg.chatType, msg.from, msg.to))

  private def resolveChatId(msg: (ConferenceId, String, UserId, Option[UserId])): Option[ChatId] = msg match {
    case (conferenceId, "conf", _, _) =>
      s"${ChatTypes.conf}@$conferenceId".some

    case (conferenceId, "dm", from, Some(to)) =>
      List(from, to).sorted.mkString(s"${ChatTypes.dm}@$conferenceId:", ":", "").some

    case _ => none
  }

  private final case class State(chats: Map[ChatId, ActorRef[ChatActorProtocol]] = Map.empty) {
    def +(chatId: ChatId, ref: ActorRef[ChatActorProtocol]): State = copy(chats + (chatId -> ref))
  }

  object protocol {
    trait ConfChatsManagerProtocol
  }
}
