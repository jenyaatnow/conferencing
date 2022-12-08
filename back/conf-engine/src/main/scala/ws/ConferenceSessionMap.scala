package com.bravewave.conferencing.conf
package ws

import akka.actor.typed.{ActorSystem, SpawnProtocol}
import com.bravewave.conferencing.conf.shared.ConferenceId

object ConferenceSessionMap {

  private var sessions: Map[ConferenceId, ConferenceSession] = Map.empty[ConferenceId, ConferenceSession]

  def findOrCreate(conferenceId: ConferenceId)(implicit system: ActorSystem[SpawnProtocol.Command]): ConferenceSession =
    sessions.getOrElse(conferenceId, create(conferenceId))

  private def create(conferenceId: ConferenceId)(implicit system: ActorSystem[SpawnProtocol.Command]) = {
    val session = ConferenceSession(conferenceId)
    sessions += conferenceId -> session
    session
  }
}
