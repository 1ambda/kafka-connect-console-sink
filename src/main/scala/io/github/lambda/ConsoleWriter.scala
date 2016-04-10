package io.github.lambda

import org.apache.kafka.connect.sink.SinkRecord


/**
 * Handle messages for a specific TopicPartition.
 */
class ConsoleWriter(connectorId: String, topic: String, partition: Int) extends Logging {
  def write(record: SinkRecord): Unit = {
    val value = record.value()

     println(value)
  }

  def close(): Unit = {
    logger.info(s"Closing ConsoleWriter connectorId: ${connectorId}, topic: ${topic}, partition: ${partition}")



    logger.info(s"Closed ConsoleWriter connectorId: ${connectorId}, topic: ${topic}, partition: ${partition}")
  }
}
