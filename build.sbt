import Path.flat
import IO._

name := "kafka-connect-console-sink"

version := "0.0.1"

scalaVersion := "2.11.8"

val LIB_VERSION_HADOOP = "2.6.0"
val LIB_VERSION_HIVE = "1.2.1"
val LIB_VERSION_KAFKA = "0.10.0.0"
val LIB_VERSION_SCALATEST = "3.0.0-M15"
val LIB_VERSION_TYPESAFE_CONFIG = "1.3.0"
val LIB_VERSION_SLF4J = "1.7.21"
val LIB_VERSION_LOGBACK = "1.1.7"
val LIB_VERSION_IVY = "2.4.0"
val LIB_VERSION_COMMONS_IO = "1.3.2"
val LIB_VERSION_SCALA_LOGGING = "3.1.0"

resolvers ++= Seq(
  "conjars" at "http://conjars.org/repo/",
  "cloudera" at "https://repository.cloudera.com/artifactory/cloudera-repos/"
)

libraryDependencies ++= Seq(
  /** scala */
  "com.typesafe" % "config" % LIB_VERSION_TYPESAFE_CONFIG,
  "org.apache.kafka" %% "kafka" % LIB_VERSION_KAFKA,
  "org.apache.kafka" % "connect-api" % LIB_VERSION_KAFKA,
  "org.apache.commons" % "commons-io" % LIB_VERSION_COMMONS_IO,
  "com.typesafe.scala-logging" %% "scala-logging" % LIB_VERSION_SCALA_LOGGING,
  "org.scalatest" %% "scalatest" % LIB_VERSION_SCALATEST % "test",

  /** java */
  "org.slf4j" % "slf4j-api" % LIB_VERSION_SLF4J,
  "ch.qos.logback" % "logback-classic" % LIB_VERSION_LOGBACK,
  "ch.qos.logback" % "logback-core" %  LIB_VERSION_LOGBACK
)

/** copy task */
val distDir = "dist"

cleanFiles <+= baseDirectory { base => base / distDir }

lazy val copyProp = taskKey[Unit]("Copy property to dist")

copyProp := {
  val log = streams.value.log
  val resourceDirPath = "src/main/resources"
  val propFileName = "connect-console-sink.properties"
  val source = file(s"${resourceDirPath}/${propFileName}")
  val target = file(s"${distDir}/${propFileName}")

  log.info(s"Copying ${source} to ${distDir}")
  copyFile(source, target)
}

/** assembly settings */
test in assembly := { /** ignore test in assembly task */ }
assemblyOutputPath in assembly := file(s"dist/${name.value}-${version.value}.jar")
assemblyMergeStrategy in assembly := {
  case PathList("org", "slf4j", xs @ _*) => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
assembly <<= assembly.dependsOn(copyProp)

