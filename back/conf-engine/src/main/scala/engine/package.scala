package com.bravewave.conferencing.conf

import com.bravewave.conferencing.conf.shared.UserId
import com.bravewave.conferencing.conf.ws.WebSocketActor.protocol.WebSocketsMessage

package object engine {

  implicit class UserSessionContextsOps(private val contexts: Set[UserSessionContext]) {
    /**
     * Notify all users.
     */
    def !!(msg: WebSocketsMessage): Unit = contexts.foreach(_ ! msg)

    /**
     * Notify all users except user with the given id.
     */
    def !-(userId: UserId, msg: WebSocketsMessage): Unit = contexts.filterNot(_.userId == userId).foreach(_ ! msg)

    /**
     * Notify user with the given id.
     */
    def !>(userId: UserId, msg: WebSocketsMessage): Unit = contexts.find(_.userId == userId).foreach(_ ! msg)

    /**
     * Removes context of user with the given id.
     */
    def -(userId: UserId): Set[UserSessionContext] = contexts.filterNot(_.userId == userId)
  }
}
