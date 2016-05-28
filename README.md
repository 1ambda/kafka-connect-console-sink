# kafka-connect-console-sink

[Kafka Connect (0.10.0.0)](http://kafka.apache.org/documentation.html#connect) Example

## Run as standalone mode

```console
$ sbt assembly

$ # configure `dist/connect-console-sink.properties` before starting connector instance

$ CLASSPATH=${CLASSPATH}:$PWD/dist/* $KAFKA_HOME/bin/connect-standalone.sh config/connect-standalone.properties $PWD/dist/connect-console-sink.properties
```

## Run as distributed mode

TODO

