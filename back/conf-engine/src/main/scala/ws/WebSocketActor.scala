package com.bravewave.conferencing.conf
package ws

import akka.actor.typed.ActorRef
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.Source
import akka.stream.typed.scaladsl.ActorSource
import com.bravewave.conferencing.conf.shared.UserId
import com.bravewave.conferencing.conf.ws.WebSocketActor.protocol._

object WebSocketActor {

  def source: Source[WebSocketsMessage, ActorRef[WebSocketsMessage]] = ActorSource.actorRef[WebSocketsMessage](
    completionMatcher = { case Complete => },
    failureMatcher = { case Failure(ex) => throw ex },
    bufferSize = 5,
    OverflowStrategy.fail,
  )


  object protocol {
    sealed trait WebSocketsMessage
    final case object Complete extends WebSocketsMessage
    final case class Failure(ex: Throwable) extends WebSocketsMessage

    sealed trait WebSocketResponse extends WebSocketsMessage
    final case class UserConnected(userId: UserId) extends WebSocketResponse
    final case class UserDisconnected(userId: UserId) extends WebSocketResponse
    final case class ConferenceDetails(connectedUsers: Set[UserId]) extends WebSocketResponse
  }
}
