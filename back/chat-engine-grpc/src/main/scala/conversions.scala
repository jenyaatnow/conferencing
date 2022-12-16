package com.bravewave.conferencing

import com.bravewave.conferencing.chatgrpc.gen.UUID

object conversions {
  implicit def grpcUuid2javaUuid(in: UUID): java.util.UUID = java.util.UUID.fromString(in.value)
  implicit def javaUuid2grpcUuid(in: java.util.UUID): UUID = UUID(in.toString)
}
