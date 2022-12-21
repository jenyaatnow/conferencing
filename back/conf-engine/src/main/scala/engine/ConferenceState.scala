package com.bravewave.conferencing.conf
package engine

import akka.actor.typed.ActorRef
import cats.implicits.catsSyntaxOptionId
import com.bravewave.conferencing.conf.shared.UserId
import com.bravewave.conferencing.conf.ws.WebSocketActor.protocol.WebSocketsMessage

import java.util.Locale

final case class ConferenceState(
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
  def !-(userId: UserId, msg: WebSocketsMessage): Unit =
    userContexts.values.filterNot(_.userId == userId).foreach(_ ! msg)

  /**
   * Notify user with the given id.
   */
  def !>(userId: UserId, msg: WebSocketsMessage): Unit =
    userContexts.get(userId).foreach(_ ! msg)

  def hasNoUsers: Boolean =
    userContexts.forall(context => !context._2.online)

  def has(userId: UserId): Boolean =
    userContexts.exists(_._1 == userId)
}

object ConferenceState {
  def empty: ConferenceState = ConferenceState()
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
