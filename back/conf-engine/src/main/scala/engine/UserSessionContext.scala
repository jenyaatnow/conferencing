package com.bravewave.conferencing.conf
package engine

import akka.actor.typed.ActorRef
import com.bravewave.conferencing.conf.shared.UserId
import com.bravewave.conferencing.conf.ws.WebSocketActor.protocol.WebSocketsMessage

final case class UserSessionContext(
  userId: UserId,
  websocket: ActorRef[WebSocketsMessage],
) {
  def !(msg: WebSocketsMessage): Unit = websocket ! msg
}
