package com.bravewave.conferencing.conf
package engine

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.bravewave.conferencing.chatgrpc.gen.{GetChatMessagesReq, KillConfChatsReq, SendMessageReq}
import com.bravewave.conferencing.client.ChatEngineClient
import com.bravewave.conferencing.conf.MessageProvider.Keys
import com.bravewave.conferencing.conf.engine.ConferenceEngineActor.protocol._
import com.bravewave.conferencing.conf.shared.ChatTypes.ChatType
import com.bravewave.conferencing.conf.shared.{ChatTypes, ConferenceId, UserId}
import com.bravewave.conferencing.conf.ws.ConferenceSessionMap
import com.bravewave.conferencing.conf.ws.WebSocketActor.protocol._
import com.bravewave.conferencing.conversions._

import java.util.UUID
import scala.util.Success

object ConferenceEngineActor {

  def apply(conferenceId: ConferenceId): Behavior[ConferenceEngineMessage] = Behaviors.setup { ctx =>
    ctx.log.info(s"Starting conference [id='$conferenceId']")
    ConferenceEngineActor(conferenceId, ConferenceState.empty)
  }

  private def apply(
    conferenceId: ConferenceId,
    state: ConferenceState,
  ): Behavior[ConferenceEngineMessage] = Behaviors.receive { (ctx, msg) =>
    implicit val system = ctx.system
    implicit val ec = system.executionContext
    val chatEngineClient = new ChatEngineClient()

    msg match {
      case Connected(newUserContext) =>
        val newUserId = newUserContext.userId

        if (state has newUserId) {
          ctx.log.error(s"User [id='$newUserId'] is already connected to conference [id='$conferenceId']")
          newUserContext.websocket
            .foreach(_ ! Error(MessageProvider.getMessage(newUserContext.locale, Keys.MultipleSimultaneousConnection)))
          Behaviors.same
        } else {
          ctx.log.info(s"User [id='$newUserId'] connected to conference [id='$conferenceId']")
          val newState = state connect newUserContext
          chatEngineClient.getChatMessages(GetChatMessagesReq(conferenceId, ChatTypes.conf.toString, newUserId, None))
            .foreach { res =>
              val confMembers =
                newState.userContexts.values.map(ctx => UserConnectionDetails(ctx.userId, ctx.username, ctx.online)).toSet
              newState !> (newUserId, ConferenceDetails(confMembers, res.messages.map(Message.apply)))
            }
          newState !- (newUserId, UserConnected(newUserId, newUserId))
          ConferenceEngineActor(conferenceId, newState)
        }

      case Disconnected(userId) =>
        ctx.log.info(s"User [id='$userId'] disconnected from conference [id='$conferenceId']")

        state !> (userId, Complete)
        state !- (userId, UserDisconnected(userId))
        val newState = state disconnect userId
        if (newState.hasNoUsers) {
          ctx.log.info(s"Finishing conference [id='$conferenceId']")
          chatEngineClient.killConfChats(KillConfChatsReq(conferenceId))
          ConferenceSessionMap.remove(conferenceId)
          Behaviors.stopped
        } else {
          ConferenceEngineActor(conferenceId, newState)
        }

      case ChatMessageReceived(id, chatType, from, to, text) =>
        chatEngineClient.sendMessage(SendMessageReq(Some(id), conferenceId, chatType.toString, from, to, text))
          .onComplete {
            case util.Failure(_) =>
            case Success(value) => state !! ChatMessages(Message(value) :: Nil)
          }
        Behaviors.same

      case Failed(ex) =>
        throw new RuntimeException(ex)
    }
  }


  object protocol {
    sealed trait ConferenceEngineMessage
    sealed trait ExternalConferenceEngineMessage extends ConferenceEngineMessage
    final case class Connected(userContext: UserSessionContext) extends ConferenceEngineMessage
    final case class Disconnected(userId: UserId) extends ConferenceEngineMessage
    final case class Failed(ex: Throwable) extends ConferenceEngineMessage

    final case class ChatMessageReceived(
      id: UUID,
      chatType: ChatType,
      from: UserId,
      to: Option[UserId],
      text: String,
    ) extends ExternalConferenceEngineMessage
  }
}
