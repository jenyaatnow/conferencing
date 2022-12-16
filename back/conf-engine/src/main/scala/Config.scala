package com.bravewave.conferencing.conf

import com.typesafe.config.{Config => TypesafeConfig, ConfigFactory}

object Config {
  val conf: TypesafeConfig = ConfigFactory.parseResources("conf-engine.conf").resolve()

  val host: String = conf.getString("app.web-server.host")
  val port: Int = conf.getInt("app.web-server.port")
}
