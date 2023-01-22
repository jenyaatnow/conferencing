package com.bravewave.conferencing.conf
package ws

import akka.actor.typed.ActorRef
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.Source
import akka.stream.typed.scaladsl.ActorSource
import com.bravewave.conferencing.conf.protocol.{Complete, Failure, WebSocketsMessage}

object WebSocketActor {

  def source: Source[WebSocketsMessage, ActorRef[WebSocketsMessage]] = ActorSource.actorRef[WebSocketsMessage](
    completionMatcher = { case Complete => },
    failureMatcher = { case Failure(ex) => throw ex },
    bufferSize = 5,
    OverflowStrategy.fail,
  )
}
