
name := "jxdr"

organization := "com.byzoro"

version := "1.0"

scalaVersion := "2.11.8"

val sparkGroupID = "org.apache.spark"
val sparkVersion = "2.1.1"
val lastVersion = "latest.integration"

//libraryDependencies += sparkGroupID %% "spark-core" % sparkVersion
libraryDependencies += sparkGroupID %% "spark-sql" % sparkVersion
// https://mvnrepository.com/artifact/com.alibaba/fastjson
libraryDependencies += "com.alibaba" % "fastjson" % "1.2.35"
