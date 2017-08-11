
name := "NetFlow"

version := "1.0"

scalaVersion := "2.11.11"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.1.1"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.1.1"
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-8" % "2.1.1"

libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "2.6.0"
libraryDependencies += "log4j" % "log4j" % "1.2.17"
