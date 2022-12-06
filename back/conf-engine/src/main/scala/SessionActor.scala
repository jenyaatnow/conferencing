package com.bravewave.conferencing.conf

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.bravewave.conferencing.conf.CoreChatEvents._
import com.bravewave.conferencing.conf.WebSocketsEvents._

object SessionActor {

  def receive(websocket: Option[ActorRef[WebSocketsEvent]]): Behavior[CoreChatEvent] = Behaviors.receiveMessage {

    case UserMessage(msg, phone) =>
      println(s"Sending message $msg to phone $phone")
      Behaviors.same

    case SMSMessage(sender, message) =>
      println("Received SMS Message!")
      websocket.foreach { socket =>
        socket ! MessageToUser(sender, message)
      }
      Behaviors.same

    case Connected(websocket) =>
      println("Received connection request!")
      receive(Some(websocket))

    case Disconnected =>
      println("Dying now!")
      Behaviors.stopped

    case Failed(ex) =>
      throw new RuntimeException(ex)
  }
}
