package io.github.lambda

import java.util

import scala.collection.mutable
import scala.collection.JavaConversions._
import scala.util.{Try, Success, Failure}

import com.typesafe.scalalogging.LazyLogging

import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.connect.sink.{SinkRecord, SinkTask}
import org.apache.kafka.connect.errors.ConnectException

/**
 * Handle multiple writers. Each writer is mapped to to only one TopicPartition
 */
class ConsoleSinkTask extends SinkTask with LazyLogging {

  var container: mutable.Map[TopicPartition, ConsoleWriter] = _
  var id: Option[String] = None

  override def start(props: util.Map[String, String]): Unit = {
    logger.info("Starting ConsoleSinkTask")

    Try {
      new ConsoleSinkConfig(props)
    } match {
      case Success(config) =>
        val id = config.getString(ConsoleSinkConfig.CONFIG_NAME_CONNECTOR_ID)
        logger.info("Initializing connector id: {}", id)

        val topicPartitions = context.assignment()
        val default = new mutable.LinkedHashMap[TopicPartition, ConsoleWriter]()

        logger.info("Creating ConsoleWriters for TopicPartitions: {}", topicPartitions.toString)

        container = topicPartitions.foldLeft(default)((acc, topicPartition) => {
          acc.put(topicPartition, new ConsoleWriter(topicPartition.topic, topicPartition.partition))
          acc
        })

      case Failure(e) =>
        val message = s"Failed to parse props in ConsoleSinkTask.start due to ${e.getMessage}"
        throw new ConnectException(message, e)
    }

    logger.info("Started ConsoleSinkTask")
  }

  override def stop(): Unit = {
    logger.info("Stopping ConsoleSinkTask")

    container.values.foreach { _.close() }

    logger.info("Stopped ConsoleSinkTask")
  }

  override def put(records: util.Collection[SinkRecord]): Unit = {
    records.foreach { record =>
      val topicPartition = new TopicPartition(record.topic, record.kafkaPartition)
      container.get(topicPartition).map { _.buffer(record) }
    }
  }

  override def flush(offsets: util.Map[TopicPartition, OffsetAndMetadata]): Unit = {
    offsets.keySet().foreach { topicPartition =>
      container.get(topicPartition).map { _.flush(offsets.get(topicPartition)) }
    }
  }

  override def onPartitionsAssigned(partitions: util.Collection[TopicPartition]): Unit = {
    logger.info("onPartitionsAssigned")

    partitions.foldLeft(container)((acc, topicPartition) => {
      if (!container.contains(topicPartition)) {
        logger.info(s"Creating writer for newly assigned TopicPartition: ${topicPartition}")
        acc.put(topicPartition, new ConsoleWriter(topicPartition.topic, topicPartition.partition))
      }

      acc
    })
  }

  override def onPartitionsRevoked(partitions: util.Collection[TopicPartition]): Unit = {
    logger.info("onPartitionsRevoked")

    /** close all partitions. we will create writers in onPartitionsAssigned */
    container.values.foreach { _.close() }
    container = new mutable.LinkedHashMap[TopicPartition, ConsoleWriter]()
  }

  override def version(): String = Version.VERSION
}
