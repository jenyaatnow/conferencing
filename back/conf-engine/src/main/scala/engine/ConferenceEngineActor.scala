package com.bravewave.conferencing.conf
package engine

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, PostStop}
import cats.implicits.catsSyntaxOptionId
import com.bravewave.conferencing.chatgrpc.gen.{GetChatMessagesReq, KillConfChatsReq, SendMessageReq}
import com.bravewave.conferencing.client.ChatEngineClient
import com.bravewave.conferencing.conf.MessageProvider.Keys
import com.bravewave.conferencing.conf.protocol._
import com.bravewave.conferencing.conf.shared.{ChatTypes, ConferenceId}
import com.bravewave.conferencing.conf.webrtc.WebRtcActor
import com.bravewave.conferencing.conf.ws.ConferenceSessionMap
import com.bravewave.conferencing.conversions._

import scala.util.Success

object ConferenceEngineActor {

  def name(confId: ConferenceId) = s"conference-engine@$confId"
  private def logPrefix(confId: ConferenceId) = s"[${name(confId)}]"

  def apply(conferenceId: ConferenceId): Behavior[ConferenceEngineProtocol] = Behaviors.setup { ctx =>
    ctx.log.info(s"${logPrefix(conferenceId)} Start")
    ConferenceEngineActor(ConferenceState(conferenceId), None)
  }

  private def apply(
    state: ConferenceState,
    webrtc: Option[ActorRef[WebRtcActorProtocol]],
  ): Behavior[ConferenceEngineProtocol] = Behaviors.receive[ConferenceEngineProtocol] { (ctx, msg) =>
    implicit val system = ctx.system
    implicit val ec = system.executionContext
    val chatEngineClient = new ChatEngineClient()
    val conferenceId = state.conferenceId

    // conference owner is the first user
    msg match {
      case Connected(newUserContext) =>
        val newUserId = newUserContext.userId

        if (state has newUserId) {
          ctx.log.error(s"${logPrefix(conferenceId)} User [id='$newUserId'] is already connected")
          newUserContext.websocket
            .foreach(_ ! Error(MessageProvider.getMessage(newUserContext.locale, Keys.MultipleSimultaneousConnection)))
          Behaviors.same
        } else {
          ctx.log.info(s"${logPrefix(conferenceId)} User [id='$newUserId'] connected")
          val newState = state connect newUserContext
          chatEngineClient.getChatMessages(GetChatMessagesReq(conferenceId, ChatTypes.conf.toString, newUserId, None))
            .foreach { res =>
              val confMembers = newState.userContexts.values
                .map(ctx => UserConnectionDetails(ctx.userId, ctx.username, ctx.online)).toSet

              newState !> (newUserId -> ConferenceDetails(confMembers, res.messages.map(Message.apply)))
            }

          newState !< (newUserId -> UserConnected(newUserId, newUserContext.username))

          if (state.hasNoUsers) {
            val webRtcActor = ctx.spawn(WebRtcActor(conferenceId, newUserId, ctx.self), WebRtcActor.name(conferenceId))
            ConferenceEngineActor(newState.copy(ownerId = newUserId.some), webRtcActor.some)
          } else {
            webrtc.foreach(_ ! WebRtcConnected(newUserId))
            ConferenceEngineActor(newState, webrtc)
          }
        }

      case Disconnected(userId) =>
        ctx.log.info(s"${logPrefix(conferenceId)} User [id='$userId'] disconnected")

        state !> (userId -> Complete)
        state !< (userId -> UserDisconnected(userId))
        val newState = state disconnect userId
        if (newState.hasNoUsers) {
          chatEngineClient.killConfChats(KillConfChatsReq(conferenceId))
          ConferenceSessionMap.remove(conferenceId)
          Behaviors.stopped
        } else {
          ConferenceEngineActor(newState, webrtc)
        }

      case !>(message) =>
        state !> message
        Behaviors.same

      case !>@(message) =>
        state !>@ message
        Behaviors.same

      case !<@(message) =>
        state !<@ message
        Behaviors.same

      case ChatMessageReceived(id, chatType, from, to, text, _) =>
        chatEngineClient.sendMessage(SendMessageReq(Some(id), conferenceId, chatType.toString, from, to, text))
          .onComplete {
            case util.Failure(_) =>
            case Success(value) => state !! ChatMessages(Message(value) :: Nil)
          }
        Behaviors.same

      case msg: WebRtcActorProtocol =>
        webrtc.foreach(_ ! msg)
        Behaviors.same

      case Failed(ex) =>
        throw new RuntimeException(ex)
    }
  }.receiveSignal {
    case (ctx, PostStop) =>
      ctx.log.info(s"${logPrefix(state.conferenceId)} Finish")
      Behaviors.same
  }
}
