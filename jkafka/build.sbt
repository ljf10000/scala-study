name := "jkafka"

version := "1.0"

organization := "com.byzoro"

scalaVersion := "2.11.8"

val sparkGroupID = "org.apache.spark"
val sparkVersion = "2.1.1"
val lastVersion = "latest.integration"

libraryDependencies += sparkGroupID %% "spark-core" % sparkVersion
libraryDependencies += sparkGroupID %% "spark-streaming" % sparkVersion
libraryDependencies += sparkGroupID %% "spark-sql" % sparkVersion
libraryDependencies += sparkGroupID %% "spark-streaming-kafka-0-8" % sparkVersion
//libraryDependencies += sparkGroupID %% "spark-streaming-kafka-0-10" % sparkVersion
libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "2.6.0"
libraryDependencies += "log4j" % "log4j" % lastVersion
libraryDependencies += "com.alibaba" % "fastjson" % "1.2.35"

// https://mvnrepository.com/artifact/org.apache.parquet/parquet-hadoop
libraryDependencies += "org.apache.parquet" % "parquet-hadoop" % "1.8.2"
