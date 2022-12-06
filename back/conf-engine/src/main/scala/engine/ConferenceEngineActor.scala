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
        // todo new user should receive basic conference data (members, chat messages etc)
        ctx.log.debug(s"User '${newUserContext.userId}' connected to conference '$conferenceId'")
        userContexts.foreach(_.websocket ! UserConnected(newUserContext.userId))
        receive(conferenceId, userContexts + newUserContext)

      case Disconnected(userId) =>
        ctx.log.debug(s"User '$userId' disconnected from conference '$conferenceId'")
        userContexts.find(_.userId == userId).foreach(_.websocket ! Complete)
        val survivors = userContexts.filterNot(_.userId == userId)
        survivors.foreach(_.websocket ! UserDisconnected(userId))
        // todo should clean session storage on last user
        receive(conferenceId, survivors)

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
