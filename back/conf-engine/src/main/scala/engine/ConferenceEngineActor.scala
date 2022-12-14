package com.bravewave.conferencing.conf
package engine

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.bravewave.conferencing.client.ChatEngineClient
import com.bravewave.conferencing.conf.engine.ConferenceEngineActor.protocol._
import com.bravewave.conferencing.conf.shared.{ConferenceId, UserId}
import com.bravewave.conferencing.conf.ws.WebSocketActor.protocol._

import scala.util.Success

object ConferenceEngineActor {

  def receive(
    conferenceId: ConferenceId,
    state: ConferenceState = ConferenceState.empty,
  ): Behavior[ConferenceEngineMessage] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Connected(newUserContext) =>
        // todo forbid users to connect multiple times
        val newUserId = newUserContext.userId
        ctx.log.info(s"User '$newUserId' connected to conference '$conferenceId'")

        implicit val system = ctx.system
        implicit val ec = system.executionContext
        val chatEngineClient = new ChatEngineClient()
        chatEngineClient.spawnChat(conferenceId).onComplete {
          case util.Failure(exception) => println(s"Grpc error: ${exception.getMessage}") // todo make logging better
          case Success(value) =>
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
