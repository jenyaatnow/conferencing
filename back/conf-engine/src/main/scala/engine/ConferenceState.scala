package com.bravewave.conferencing.conf
package engine

import akka.actor.typed.ActorRef
import com.bravewave.conferencing.conf.shared.UserId
import com.bravewave.conferencing.conf.ws.WebSocketActor.protocol.WebSocketsMessage

final case class ConferenceState(
  userContexts: Map[UserId, UserSessionContext] = Map.empty,
) {
  def connect(ctx: UserSessionContext): ConferenceState =
    copy(userContexts = userContexts + (ctx.userId -> ctx))

  def disconnect(userId: UserId): ConferenceState =
    copy(userContexts = userContexts + (userId -> UserSessionContext(userId, online = false)))

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
}

object ConferenceState {
  def empty: ConferenceState = ConferenceState()
}


final case class UserSessionContext(
  userId: UserId,
  websocket: Option[ActorRef[WebSocketsMessage]] = None,
  online: Boolean = true,
) {
  def !(msg: WebSocketsMessage): Unit = websocket.foreach(_ ! msg)
}
