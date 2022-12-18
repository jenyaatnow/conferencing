package com.bravewave.conferencing.chat

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.bravewave.conferencing.chat.ChatEngineDispatcher.protocol._
import com.bravewave.conferencing.chat.ConfChatsManager.protocol.ConfChatsManagerProtocol
import com.bravewave.conferencing.chatgrpc.gen.{GetChatMessagesRes, KillConfChatsRes}
import com.bravewave.conferencing.conf.shared.ConferenceId

object ChatEngineDispatcher {

  def newInstance: Behavior[ChatEngineDispatcherProtocol] = ChatEngineDispatcher()

  private def apply(state: State = State()): Behavior[ChatEngineDispatcherProtocol] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case message @ SendChatMessage(in, _) =>
        val confChatsManagerRef = state.confChats.getOrElse(
          in.conferenceId,
          ctx.spawn(ConfChatsManager.newInstance, s"conf_chats_manager_${in.conferenceId}")
        )
        confChatsManagerRef ! message
        ChatEngineDispatcher(state + (in.conferenceId, confChatsManagerRef))

      case message @ GetChatMessages(in, replyTo) =>
        state.confChats.get(in.conferenceId).map(_ ! message).getOrElse(replyTo ! GetChatMessagesRes())
        Behaviors.same

      case KillConfChats(conferenceId, replyTo) =>
        state.confChats.get(conferenceId).foreach(ctx.stop)
        replyTo ! KillConfChatsRes(conferenceId)
        ChatEngineDispatcher(state - conferenceId)

      case _ => Behaviors.same
    }
  }

  private final case class State(confChats: Map[ConferenceId, ActorRef[ConfChatsManagerProtocol]] = Map.empty) {
    def +(confId: ConferenceId, ref: ActorRef[ConfChatsManagerProtocol]): State = copy(confChats + (confId -> ref))
    def -(confId: ConferenceId): State = copy(confChats - confId)
  }

  object protocol {
    trait ChatEngineDispatcherProtocol
    final case class KillConfChats(conferenceId: ConferenceId, replyTo: ActorRef[KillConfChatsRes]) extends ChatEngineDispatcherProtocol
  }
}
