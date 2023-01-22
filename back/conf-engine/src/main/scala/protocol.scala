package com.bravewave.conferencing.conf

import cats.implicits.catsSyntaxOptionId
import com.bravewave.conferencing.chatgrpc.gen.ChatMessageRes
import com.bravewave.conferencing.conf.engine.UserSessionContext
import com.bravewave.conferencing.conf.shared.ChatTypes.ChatType
import com.bravewave.conferencing.conf.shared.{ChatTypes, UserId}
import com.bravewave.conferencing.conversions._
import io.circe.Json

import java.time.{Instant, ZoneId, ZonedDateTime}
import java.util.UUID

object protocol {

  sealed trait ConferenceEngineProtocol
  sealed trait InternalConferenceEngineProtocol extends ConferenceEngineProtocol
  sealed trait ExternalConferenceEngineProtocol extends ConferenceEngineProtocol {
    val senderId: Option[UserId]

    def withSenderId(senderId: UserId): ExternalConferenceEngineProtocol
  }

  final case class Connected(userContext: UserSessionContext) extends InternalConferenceEngineProtocol
  final case class Disconnected(userId: UserId) extends InternalConferenceEngineProtocol
  final case class Failed(ex: Throwable) extends InternalConferenceEngineProtocol

  final case class !>(msg: (UserId, WebSocketResponse)) extends InternalConferenceEngineProtocol
  final case class !>@(msg: WebSocketResponse) extends InternalConferenceEngineProtocol
  final case class !<@(msg: WebSocketResponse) extends InternalConferenceEngineProtocol

  final case class ChatMessageReceived(
    id: UUID,
    chatType: ChatType,
    from: UserId,
    to: Option[UserId],
    text: String,
    senderId: Option[UserId] = None,
  ) extends ExternalConferenceEngineProtocol {
    override def withSenderId(senderId: UserId): ExternalConferenceEngineProtocol = copy(senderId = senderId.some)
  }


  sealed trait WebRtcActorProtocol

  final case class WebRtcConnected(userId: UserId) extends WebRtcActorProtocol

  final case class WebRtcOffer(offer: Json, senderId: Option[UserId] = None)
    extends WebRtcActorProtocol with WebSocketResponse with ExternalConferenceEngineProtocol {
    override def withSenderId(senderId: UserId): ExternalConferenceEngineProtocol = copy(senderId = senderId.some)
  }

  final case class WebRtcAnswer(answer: Json, senderId: Option[UserId] = None)
    extends WebRtcActorProtocol with WebSocketResponse with ExternalConferenceEngineProtocol {
    override def withSenderId(senderId: UserId): ExternalConferenceEngineProtocol = copy(senderId = senderId.some)
  }


  sealed trait WebSocketsMessage
  sealed trait WebSocketResponse extends WebSocketsMessage

  final case object Complete extends WebSocketsMessage
  final case class Failure(ex: Throwable) extends WebSocketsMessage

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
