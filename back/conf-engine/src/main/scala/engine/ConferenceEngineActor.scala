package com.bravewave.conferencing.conf
package engine

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.bravewave.conferencing.conf.engine.ConferenceEngineActor.protocol._
import com.bravewave.conferencing.conf.ws.WebSocketActor.protocol._

object ConferenceEngineActor {
  // todo spawn chat-actor
  // todo spawn plugins-engine-actor

  def receive(
    conferenceId: ConferenceId,
    userContexts: Set[UserSessionContext] = Set.empty,
  ): Behavior[ConferenceEngineMessage] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Connected(newUserContext) =>
        // todo forbid users to connect multiple times
        val newUserId = newUserContext.userId
        ctx.log.debug(s"User '$newUserId' connected to conference '$conferenceId'")

        val newUserContexts = userContexts + newUserContext
        newUserContexts !> (newUserId, ConferenceDetails(newUserContexts.map(_.userId)))
        newUserContexts !- (newUserId, UserConnected(newUserId))
        receive(conferenceId, newUserContexts)

      case Disconnected(userId) =>
        // todo should clean session storage on last user
        ctx.log.debug(s"User '$userId' disconnected from conference '$conferenceId'")

        userContexts !> (userId, Complete)
        userContexts !- (userId, UserDisconnected(userId))
        receive(conferenceId, userContexts - userId)

      case UserMessage(msg, phone) =>
        println(s"Sending message $msg to phone $phone")
        Behaviors.same

      case Failed(ex) =>
        throw new RuntimeException(ex)
    }
  }


  object protocol {
    sealed trait ConferenceEngineMessage
    final case class Connected(userContext: UserSessionContext) extends ConferenceEngineMessage
    final case class Disconnected(userId: UserId) extends ConferenceEngineMessage
    final case class UserMessage(message: String, phoneNumber: String) extends ConferenceEngineMessage
    final case class Failed(ex: Throwable) extends ConferenceEngineMessage
  }
}
