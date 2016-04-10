package io.github.lambda

import java.util
import org.apache.kafka.connect.errors.ConnectException

import collection.JavaConversions._
import scala.util.{Try, Failure, Success}

import org.apache.kafka.connect.connector.{Task, Connector}

class ConsoleSinkConnector extends Connector with Logging {
  private var configProps : util.Map[String, String] = null

  override def taskClass(): Class[_ <: Task] = classOf[ConsoleSinkTask]

  override def taskConfigs(maxTasks: Int): util.List[util.Map[String, String]] = {
    logger.info(s"Setting task configurations for $maxTasks workers.")

    (1 to maxTasks).map(c => configProps)
  }

  override def start(props: util.Map[String, String]): Unit = {
    Try {
      new ConsoleSinkConfig(props)
      configProps = props
    } match {
      case Failure(e) =>
        throw new ConnectException(s"Couldn't start ConsoleConnector due to configuration error: ${e.getMessage}", e)

      case _ =>
    }
  }

  override def stop(): Unit = {}

  override def version(): String = Version.VERSION
}
