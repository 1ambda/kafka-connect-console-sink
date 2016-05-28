package io.github.lambda

import java.util
import collection.JavaConversions._

import org.apache.kafka.connect.connector.{Connector, Task}
import com.typesafe.scalalogging.LazyLogging

class ConsoleSinkConnector extends Connector with LazyLogging {
  import ConsoleSinkConfig._

  private var props: util.Map[String, String] = _

  override def taskClass(): Class[_ <: Task] = classOf[ConsoleSinkTask]

  override def taskConfigs(maxTasks: Int): util.List[util.Map[String, String]] = {
    logger.info("Setting task configurations for {} workers.", maxTasks.toString)

    (1 to maxTasks).map(taskId => props)
  }

  override def start(props: util.Map[String, String]): Unit = {
    this.props = props
  }

  override def stop(): Unit = {}

  override def version(): String = Version.VERSION

  override def config() = definition
}

