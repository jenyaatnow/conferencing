package com.bravewave.conferencing.conf

package object shared {
  type ChatId = String
  type UserId = String
  type ConferenceId = String

  object ChatTypes extends Enumeration {
    type ChatType = Value
    val conf, dm = Value
  }

  final case class User(userId: UserId, username: String)
}
