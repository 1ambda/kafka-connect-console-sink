package io.github.lambda

import java.util
import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.connect.errors.ConnectException
import org.slf4j.LoggerFactory

import collection.JavaConversions._
import scala.util.{Try, Failure, Success}

import org.apache.kafka.connect.connector.{Task, Connector}

class ConsoleSinkConnector extends Connector with LazyLogging {
  private var configProps : util.Map[String, String] = null

  override def taskClass(): Class[_ <: Task] = classOf[ConsoleSinkTask]

  override def taskConfigs(maxTasks: Int): util.List[util.Map[String, String]] = {
    logger.info("Setting task configurations for {} workers.", maxTasks.toString)

    (1 to maxTasks).map(c => configProps)
  }

  override def start(props: util.Map[String, String]): Unit = {
    Try {
      new ConsoleSinkConfig(props)
      configProps = props
    } match {
      case Failure(e) =>
        val message = s"Couldn't start ConsoleSinkConnector due to configuration error: ${e.getMessage}"
        throw new ConnectException(message, e)

      case _ =>
    }
  }

  override def stop(): Unit = {}

  override def version(): String = Version.VERSION
}
