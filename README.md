# kafka-connect-console-sink

[Kafka Connect](http://kafka.apache.org/documentation.html#connect) Example

```console
$ sbt assembly

$ # configure `dist/connect-console-sink.properties` before starting connector instance

$ export CLASSPATH=$PWD/dist/kafka-connect-console-sink-0.0.1.jar:$CLASSPATH
$ $KAFKA_HOME/bin/connect-standalone.sh config/connect-standalone.properties ./dist/connect-console-sink.properties
```
