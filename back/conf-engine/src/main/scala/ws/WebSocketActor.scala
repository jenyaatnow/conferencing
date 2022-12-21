package com.bravewave.conferencing.conf
package ws

import akka.actor.typed.ActorRef
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.Source
import akka.stream.typed.scaladsl.ActorSource
import com.bravewave.conferencing.chatgrpc.gen.ChatMessageRes
import com.bravewave.conferencing.conf.shared.ChatTypes.ChatType
import com.bravewave.conferencing.conf.shared._
import com.bravewave.conferencing.conf.ws.WebSocketActor.protocol._
import com.bravewave.conferencing.conversions._

import java.time.{Instant, ZoneId, ZonedDateTime}
import java.util.UUID

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
    final case class Error(message: String, timestamp: ZonedDateTime = ZonedDateTime.now()) extends WebSocketResponse
    final case class UserConnected(userId: UserId, username: String) extends WebSocketResponse
    final case class UserDisconnected(userId: UserId) extends WebSocketResponse
    final case class ConferenceDetails(users: Set[UserConnectionDetails], chatMessages: Seq[Message]) extends WebSocketResponse
    final case class ChatMessages(messages: List[Message]) extends WebSocketResponse

    final case class Message(
      id: Option[UUID],
      chatType: ChatType,
      from: UserId,
      to: Option[UserId],
      text: String,
      timestamp: Option[ZonedDateTime],
    )
    object Message {
      def apply(m: ChatMessageRes): Message = Message(
        m.id.map(grpcUuid2javaUuid),
        ChatTypes.withName(m.chatType),
        m.from,
        m.to,
        m.text,
        m.timestamp.map(ts => Instant.ofEpochSecond(ts.seconds, ts.nanos).atZone(ZoneId.of("UTC")))
      )
    }

    final case class UserConnectionDetails(userId: UserId, username: String, online: Boolean)
  }
}
