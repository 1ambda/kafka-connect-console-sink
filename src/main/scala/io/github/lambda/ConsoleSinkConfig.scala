package io.github.lambda

import java.util

import org.apache.kafka.common.config.{ConfigDef, AbstractConfig}
import org.apache.kafka.common.config.ConfigDef.{Type, Importance}

class ConsoleSinkConfig(props: util.Map[String, String])
  extends AbstractConfig(ConsoleSinkConfig.definition, props)

object ConsoleSinkConfig {

  val CONFIG_NAME_CONNECTOR_ID = "id"
  val CONFIG_DOCS_CONNECTOR_ID = "Connector ID"

  val definition: ConfigDef = new ConfigDef()
    .define(CONFIG_NAME_CONNECTOR_ID, Type.STRING, Importance.HIGH, CONFIG_DOCS_CONNECTOR_ID)
}
