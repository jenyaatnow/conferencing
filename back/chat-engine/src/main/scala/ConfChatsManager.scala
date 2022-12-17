package com.bravewave.conferencing.chat

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import cats.implicits.{catsSyntaxOptionId, none}
import com.bravewave.conferencing.chat.ChatActor.protocol.ChatActorProtocol
import com.bravewave.conferencing.chat.ConfChatsManager.protocol.ConfChatsManagerProtocol
import com.bravewave.conferencing.chatgrpc.gen.ChatMessageRequest
import com.bravewave.conferencing.conf.shared.{ChatId, ChatTypes, ConferenceId}

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

      case _ => Behaviors.same
    }
  }

  private def spawnChat(ctx: ActorContext[ConfChatsManagerProtocol], chatId: String): ActorRef[ChatActorProtocol] = {
    val chatActor = ctx.spawn(ChatActor.newInstance, s"chat-$chatId")
    ctx.log.info(s"Spawned new chat '$chatId'")
    chatActor
  }

  private def resolveChatId(conferenceId: ConferenceId): String = s"${ChatTypes.conf}@$conferenceId"

  private def resolveChatId(msg: ChatMessageRequest): Option[ChatId] = msg match {
    case ChatMessageRequest(_, conferenceId, "conf", _, _, _, _) =>
      s"${ChatTypes.conf}@$conferenceId".some

    case ChatMessageRequest(_, conferenceId, "dm", from, Some(to), _, _) =>
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
