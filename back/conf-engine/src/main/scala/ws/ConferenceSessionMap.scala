package com.bravewave.conferencing.conf
package ws

import akka.actor.typed.{ActorSystem, SpawnProtocol}
import com.bravewave.conferencing.conf.shared.ConferenceId

import java.util.concurrent.ConcurrentHashMap

object ConferenceSessionMap {

  private val sessions: ConcurrentHashMap[ConferenceId, ConferenceSession] = new ConcurrentHashMap()

  def findOrCreate(conferenceId: ConferenceId)(implicit system: ActorSystem[SpawnProtocol.Command]): ConferenceSession =
    sessions.computeIfAbsent(conferenceId, ConferenceSession.apply)

  def remove(conferenceId: ConferenceId): Unit = sessions.remove(conferenceId)
}
