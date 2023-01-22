package com.bravewave.conferencing.conf
package engine

import akka.actor.typed.ActorRef
import cats.implicits.catsSyntaxOptionId
import com.bravewave.conferencing.conf.protocol.WebSocketsMessage
import com.bravewave.conferencing.conf.shared.{ConferenceId, UserId}

import java.util.Locale

final case class ConferenceState(
  conferenceId: ConferenceId,
  ownerId: Option[UserId] = None,
  userContexts: Map[UserId, UserSessionContext] = Map.empty,
) {

  def connect(ctx: UserSessionContext): ConferenceState =
    copy(userContexts = userContexts + (ctx.userId -> ctx))

  def disconnect(userId: UserId): ConferenceState =
    copy(userContexts = userContexts ++ userContexts.get(userId).map(userId -> _.offline()))

  /**
   * Notify all users.
   */
  def !!(msg: WebSocketsMessage): Unit =
    userContexts.values.foreach(_ ! msg)

  /**
   * Notify all users except user with the given id.
   */
  def !<(payload: (UserId, WebSocketsMessage)): Unit =
    userContexts.values.filterNot(_.userId == payload._1).foreach(_ ! payload._2)

  /**
   * Notify user with the given id.
   */
  def !>(payload: (UserId, WebSocketsMessage)): Unit =
    userContexts.get(payload._1).foreach(_ ! payload._2)

  /**
   * Notify owner.
   */
  def !>@(msg: WebSocketsMessage): Unit =
    ownerId.flatMap(userContexts.get).foreach(_ ! msg)

  /**
   * Notify all users except owner.
   */
  def !<@(msg: WebSocketsMessage): Unit =
    ownerId.map(oid => userContexts.values.filterNot(_.userId == oid)).getOrElse(Nil).foreach(_ ! msg)

  def hasNoUsers: Boolean =
    userContexts.forall(context => !context._2.online)

  def has(userId: UserId): Boolean =
    userContexts.exists(_._1 == userId)
}


final case class UserSessionContext(
  userId: UserId,
  username: String,
  locale: Locale,
  websocket: Option[ActorRef[WebSocketsMessage]],
  online: Boolean = true,
) {
  def !(msg: WebSocketsMessage): Unit = websocket.foreach(_ ! msg)
  def offline(): UserSessionContext = copy(online = false, websocket = None)
}

object UserSessionContext {
  def online(
    userId: UserId,
    username: String,
    locale: String,
    websocket: ActorRef[WebSocketsMessage],
  ): UserSessionContext = UserSessionContext(
    userId,
    username,
    new Locale.Builder().setLanguageTag(locale).build(),
    websocket.some,
  )
}
