package com.bravewave.conferencing.conf
package webrtc

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, PostStop}
import cats.implicits.catsSyntaxOptionId
import com.bravewave.conferencing.conf.protocol._
import com.bravewave.conferencing.conf.shared.{ConferenceId, UserId}

object WebRtcActor {
  def name(confId: ConferenceId) = s"webrtc@$confId"
  private def logPrefix(confId: ConferenceId) = s"[${name(confId)}]"

  def apply(
    conferenceId: ConferenceId,
    ownerId: UserId,
    replyTo: ActorRef[ConferenceEngineProtocol],
  ): Behavior[WebRtcActorProtocol] = Behaviors.setup { ctx =>
    ctx.log.info(s"${logPrefix(conferenceId)} Start")
    WebRtcActor(conferenceId, ownerId, replyTo, State())
  }

  private def apply(
    conferenceId: ConferenceId,
    ownerId: UserId,
    replyTo: ActorRef[ConferenceEngineProtocol],
    state: State,
  ): Behavior[WebRtcActorProtocol] = Behaviors.receive[WebRtcActorProtocol] { (ctx, msg) =>
    def behavior = apply(conferenceId, ownerId, replyTo, _: State)

    msg match {
      case offer @ WebRtcOffer(_, senderOpt) =>
        senderOpt match {
          case Some(senderId) if senderId == ownerId =>
            replyTo ! !<@(offer)
            behavior(state.copy(offer = offer.some))

          case Some(senderId) =>
            ctx.log.error(s"${logPrefix(conferenceId)} User [id='$senderId'] is not conference owner")
            Behaviors.same

          case None =>
            ctx.log.error(s"${logPrefix(conferenceId)} Unknown sender id")
            Behaviors.same
        }

      case answer: WebRtcAnswer =>
        replyTo ! !>@(answer)
        Behaviors.same

      case WebRtcConnected(userId) =>
        if (userId != ownerId) state.offer.foreach(offer => replyTo ! !>(userId -> offer))
        Behaviors.same
    }
  }.receiveSignal {
    case (ctx, PostStop) =>
      ctx.log.info(s"${logPrefix(conferenceId)} Finish")
      Behaviors.same
  }

  private final case class State(
    offer: Option[WebRtcOffer] = None,
  )
}
