package io.github.lambda

import java.util

import scala.collection.mutable
import scala.collection.JavaConversions._
import scala.util.{Try, Success, Failure}

import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.connect.sink.{SinkRecord, SinkTask}
import org.apache.kafka.connect.errors.ConnectException

/**
 * Handle multiple writers. Each writer is mapped to to its own TopicPartition
 */
class ConsoleSinkTask extends SinkTask with Logging {

  /** Container for Writers */
  val container: mutable.Map[TopicPartition, ConsoleWriter] = new mutable.LinkedHashMap()

  override def start(props: util.Map[String, String]): Unit = {
    logger.info("Starting all writers")

    Try {
      new ConsoleSinkConfig(props)
    } match {
      case Success(config) =>
        val id = config.getString(ConsoleSinkConfig.CONFIG_NAME_CONNECTOR_ID)

        val topicPartitions = context.assignment()

        topicPartitions.foldLeft(container)((container, topicPartition) => {
          container.put(topicPartition, new ConsoleWriter(id, topicPartition.topic, topicPartition.partition))
          container
        })

      case Failure(e) =>
        throw new ConnectException(s"Failed to parse props in ConsoleSinkTask.start, ${e.getMessage}", e)
    }

    logger.info("Started all writers")
  }

  override def stop(): Unit = {
    container.values.foreach { topicPartitionWriter =>
      topicPartitionWriter.close()
    }
  }

  override def put(records: util.Collection[SinkRecord]): Unit = {
    records.foreach { record =>
      val topicPartition = new TopicPartition(record.topic, record.kafkaPartition)

      container.get(topicPartition).map { _.write(record) }
      println(record.value())
    }
  }

  override def flush(offsets: util.Map[TopicPartition, OffsetAndMetadata]): Unit = {
  }

  override def version(): String = Version.VERSION
}
