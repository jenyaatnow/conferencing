package com.bravewave.conferencing.conf
package ws

import akka.actor.typed.SpawnProtocol.Spawn
import akka.actor.typed._
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.FlowShape
import akka.stream.scaladsl._
import akka.stream.typed.scaladsl.ActorSink
import akka.util.Timeout
import com.bravewave.conferencing.conf.engine.ConferenceEngineActor.protocol._
import com.bravewave.conferencing.conf.engine.{ConferenceEngineActor, UserSessionContext}
import com.bravewave.conferencing.conf.shared.ChatTypes.ChatType
import com.bravewave.conferencing.conf.shared.{ChatTypes, ConferenceId, UserId}
import com.bravewave.conferencing.conf.ws.WebSocketActor.protocol._
import io.circe._
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.auto._
import io.circe.generic.extras.semiauto._
import io.circe.jawn.decode
import io.circe.syntax._

import scala.concurrent.Future
import scala.concurrent.duration._


class ConferenceSession(conferenceId: ConferenceId)(implicit system: ActorSystem[SpawnProtocol.Command]) {
  private val conferenceSessionName = s"conference-session@$conferenceId"

  private implicit val timeout: Timeout = Timeout(3.seconds)
  private implicit val ec = system.dispatchers.lookup(DispatcherSelector.default())

  // asks to spawn an actor outside of the system
  private[this] val sessionActor: Future[ActorRef[ConferenceEngineMessage]] =
    system.ask[ActorRef[ConferenceEngineMessage]] { ref =>
      Spawn[ConferenceEngineMessage](
        behavior = ConferenceEngineActor(conferenceId),
        name = conferenceSessionName,
        props = Props.empty,
        replyTo = ref
      )
    }

  // because we have access to an actor in the future, we also only have access to this Flow in the future
  def webflow(userId: UserId, username: String, locale: String): Future[Flow[Message, Message, _]]  = sessionActor.map { session =>
    Flow.fromGraph(
      // passing parameters allows us to instantiate them as stream resource inside the stream
      GraphDSL.createGraph(WebSocketActor.source) { implicit builder => socket =>

        import GraphDSL.Implicits._

        // transforms messages from the websockets into the actor's protocol
        val webSocketSource = builder.add(
          Flow[Message].collect {
            case TextMessage.Strict(json) =>
              implicit val chatTypeDecoder: Decoder[ChatType] = Decoder.decodeEnumeration(ChatTypes)
              implicit val configuration: Configuration = Configuration.default.withDiscriminator("type")
              implicit val decoder: Decoder[ExternalConferenceEngineMessage] = deriveConfiguredDecoder[ExternalConferenceEngineMessage]
              decode[ExternalConferenceEngineMessage](json).getOrElse(Failed(new RuntimeException(s"Unable to parse incoming message:\n$json")))
          }
        )

        // transform a message from the WebSocketProtocol back into a websocket text message
        val webSocketSink = builder.add(
          Flow[WebSocketsMessage].collect {
            case r: WebSocketResponse =>
              implicit val chatTypeEncoder: Encoder[ChatType] = Encoder.encodeEnumeration(ChatTypes)
              implicit val configuration: Configuration = Configuration.default.withDiscriminator("type")
              implicit val encoder: Encoder[WebSocketResponse] = deriveConfiguredEncoder[WebSocketResponse]
              TextMessage(r.asJson.noSpaces)
          }
        )

        // route messages to the session actor
        val routeToSession = builder.add(ActorSink.actorRef[ConferenceEngineMessage](
          ref = session,
          onCompleteMessage = Disconnected(userId),
          onFailureMessage = Failed.apply
        ))

        // materialize the Source we supplied in the argument
        val materializedActorSource =
          builder.materializedValue.map(ref => Connected(UserSessionContext.online(userId, username, locale, ref)))

        // fan-in - combine two sources into one
        val merge = builder.add(Merge[ConferenceEngineMessage](2))
        // ~> connects everything
        webSocketSource ~> merge.in(0)
        materializedActorSource ~> merge.in(1)
        merge ~> routeToSession
        socket ~> webSocketSink
        // expose inlets/outlets
        FlowShape(webSocketSource.in, webSocketSink.out)
      }
    )
  }
}

object ConferenceSession {
  def apply(conferenceId: ConferenceId)(implicit system: ActorSystem[SpawnProtocol.Command]): ConferenceSession =
    new ConferenceSession(conferenceId)
}
