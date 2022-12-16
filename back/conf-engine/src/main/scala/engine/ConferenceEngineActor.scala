package com.bravewave.conferencing.conf
package engine

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.bravewave.conferencing.chatgrpc.gen.ChatMessageRequest
import com.bravewave.conferencing.client.ChatEngineClient
import com.bravewave.conferencing.conf.engine.ConferenceEngineActor.protocol._
import com.bravewave.conferencing.conf.shared.ChatTypes.ChatType
import com.bravewave.conferencing.conf.shared.{ConferenceId, UserId}
import com.bravewave.conferencing.conf.ws.WebSocketActor.protocol._
import com.bravewave.conferencing.conversions._

import java.util.UUID
import scala.util.Success

object ConferenceEngineActor {

  def receive(
    conferenceId: ConferenceId,
    state: ConferenceState = ConferenceState.empty,
  ): Behavior[ConferenceEngineMessage] = Behaviors.receive { (ctx, msg) =>
    implicit val system = ctx.system
    implicit val ec = system.executionContext
    val chatEngineClient = new ChatEngineClient()

    msg match {
      case Connected(newUserContext) =>
        // todo forbid users to connect multiple times
        val newUserId = newUserContext.userId
        ctx.log.info(s"User '$newUserId' connected to conference '$conferenceId'")

        if (state.isClear) {
          chatEngineClient.spawnChat(conferenceId).onComplete {
            case util.Failure(exception) => println(s"Grpc error: ${exception.getMessage}") // todo make logging better
            case Success(value) =>
          }
        }

        val newState = state connect newUserContext
        newState !> (
          newUserId,
          ConferenceDetails(newState.userContexts.values.map(ctx => UserConnectionDetails(ctx.userId, ctx.userId, ctx.online)).toSet)
        )
        newState !- (newUserId, UserConnected(newUserId, newUserId))
        receive(conferenceId, newState)

      case Disconnected(userId) =>
        // todo should clean session storage on last user
        // todo don't forget to kill all related chats on last user
        ctx.log.info(s"User '$userId' disconnected from conference '$conferenceId'")

        state !> (userId, Complete)
        state !- (userId, UserDisconnected(userId))
        receive(conferenceId, state disconnect userId)

      case ChatMessageReceived(id, chatType, from, to, text) =>
        chatEngineClient.sendMessage(ChatMessageRequest(Some(id), conferenceId, chatType.toString, from, to, text))
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
