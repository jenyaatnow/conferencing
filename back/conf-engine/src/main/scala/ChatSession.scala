package com.bravewave.conferencing.conf

import akka.actor.typed.SpawnProtocol.Spawn
import akka.actor.typed._
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl._
import akka.stream.typed.scaladsl.{ActorSink, ActorSource}
import akka.stream.{FlowShape, OverflowStrategy}
import akka.util.Timeout
import com.bravewave.conferencing.conf.ChatSession._
import com.bravewave.conferencing.conf.CoreChatEvents._
import com.bravewave.conferencing.conf.WebSocketsEvents._

import scala.concurrent.Future
import scala.concurrent.duration._



class ChatSession(userId: String)(implicit system: ActorSystem[SpawnProtocol.Command]) {
  println("Spawning new chat session")
  implicit val timeout: Timeout = Timeout(3.seconds)
  implicit val ec = system.dispatchers.lookup(DispatcherSelector.default())

  // asks to spawn an actor outside of the system
  private[this] val sessionActor: Future[ActorRef[CoreChatEvent]] =
    system.ask[ActorRef[CoreChatEvent]] { ref =>
      Spawn[CoreChatEvent](
        // initial behavior has no websockets connection
        behavior = SessionActor.receive(None),
        name = s"$userId-chat-session",
        props = Props.empty,
        replyTo = ref
      )
    }

  // because we have access to an actor in the future, we also only have access to this Flow in the future
  def webflow(): Future[Flow[Message, Message, _]]  = sessionActor.map { session =>
    Flow.fromGraph(
      // passing parameters allows us to instantiate them as stream resource inside the stream
      GraphDSL.createGraph(webSocketsActor) { implicit builder => socket =>

        import GraphDSL.Implicits._

        // transforms messages from the websockets into the actor's protocol
        val webSocketSource = builder.add(
          Flow[Message].collect {
            case TextMessage.Strict(txt) => UserMessage(txt, "111-111-1111")
          }
        )

        // transform a message from the WebSocketProtocol back into a websocket text message
        val webSocketSink = builder.add(
          Flow[WebSocketsEvent].collect {
            case MessageToUser(p, t) =>
              TextMessage(p + t)
          }
        )

        // route messages to the session actor
        val routeToSession = builder.add(ActorSink.actorRef[CoreChatEvent](
          ref = session,
          onCompleteMessage = Disconnected,
          onFailureMessage = Failed.apply
        ))

        // materialize the Source we supplied in the argument
        val materializedActorSource = builder.materializedValue.map(ref => Connected(ref))

        // fan-in - combine two sources into one
        val merge = builder.add(Merge[CoreChatEvent](2))
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

object ChatSession {
  def apply(userId: String)(implicit system: ActorSystem[SpawnProtocol.Command]): ChatSession = new ChatSession(userId)

  private val webSocketsActor = ActorSource.actorRef[WebSocketsEvent](
    completionMatcher = { case Complete => },
    failureMatcher = { case WebSocketsEvents.Failure(ex) => throw ex },
    bufferSize = 5,
    OverflowStrategy.fail
  )
}
