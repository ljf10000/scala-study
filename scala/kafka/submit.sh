#!/bin/bash

readonly JAR_SPARK_STREAMING_KAFKA='spark-streaming-kafka-0-8-assembly_2.11-2.1.1.jar'

#        --conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file:log4j.properties" \
#        --files $(pwd)/log4j.properties \

spark-submit \
	--master yarn \
	--deploy-mode cluster \
	--driver-memory 1g \
	--executor-memory 1g \
	--num-executors 5 \
	--executor-cores 4 \
	--conf spark.default.parallelism=60 \
	--conf spark.storage.memoryFraction=0.4 \
	--conf spark.shuffle.memoryFraction=0.4 \
	--conf spark.yarn.executor.memoryOverhead=1024 \
	--conf spark.yarn.driver.memoryOverhead=1024 \
	--conf spark.streaming.kafka.maxRatePerPartition=1000 \
	--conf spark.cleaner.referenceTracking.blocking=true \
	--conf spark.streaming.concurrentJobs=16 \
	--conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=log4j.properties" \
	--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.properties" \
	--files $(pwd)/src/main/resources/log4j.properties \
	--jars ${SPARK_HOME}/extrajars/${JAR_SPARK_STREAMING_KAFKA} \
	--class "NetFlow" \
	target/scala-2.11/netflow_2.11-1.0.jar
