package com.bravewave.conferencing.conf

import com.typesafe.config.{Config, ConfigFactory}

import java.util.Locale
import scala.collection.mutable

object MessageProvider {
  private type Language = String

  private val default: Config = ConfigFactory.parseResources("messages/en.conf")
  private val conf = new mutable.HashMap[Language, Config]()

  private def getLocalizedConfOrDefault(locale: Locale) = {
    val localizedConf = ConfigFactory.parseResources(s"messages/${locale.getLanguage}.conf")
    conf.getOrElseUpdate(locale.getLanguage, if (localizedConf.isEmpty) default else localizedConf)
  }

  def getMessage(locale: Locale, key: String): String = getLocalizedConfOrDefault(locale).getString(key)

  object Keys {
    val MultipleSimultaneousConnection = "error.multiple-simultaneous-connection"
  }
}
