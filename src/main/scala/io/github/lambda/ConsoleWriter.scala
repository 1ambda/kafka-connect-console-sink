package io.github.lambda

import java.io.{OutputStreamWriter, BufferedWriter}

import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.io.IOUtils
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.connect.sink.SinkRecord

import scala.util.{Failure, Try}

class ConsoleWriter(topic: String, partition: Long) extends LazyLogging {
  
  val ENCODING_UTF8 = "UTF-8"

  private val outputStream = new BufferedWriter(new OutputStreamWriter(System.out))

  def buffer(record: SinkRecord): Unit = {
    logger.debug("Buffered record in ConsoleWriter({})", getWriterId)
    outputStream.write(s"${record.value}\n")
  }

  def flush(offset: OffsetAndMetadata): Unit = {
    logger.debug("Flushing records in ConsoleWriter({})", getWriterId)
    outputStream.flush()
  }

  def close(): Unit = {
    logger.debug("Closing ConsoleWriter({})", getWriterId)


    /** do not close outputStream since it is System.out */
    // IOUtils.closeQuietly(outputStream)

    logger.debug("Closed ConsoleWriter({})", getWriterId)
  }

  def getWriterId: String = s"${topic}-${partition}"
}
