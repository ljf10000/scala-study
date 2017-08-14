#!/bin/bash

readonly SPARK_EXT=${SPARK_HOME}/extrajars
readonly JAR_SPARK_STREAMING_KAFKA='spark-streaming-kafka-0-8-assembly_2.11-2.1.1.jar'
readonly JAR_TARGET='target/scala-2.11/jkafka_2.11-1.0.jar'
readonly JAR_JXDR='../jxdr/target/scala-2.11/jxdr_2.11-1.0.jar'
readonly TOPICS='xdr,xdrFile,xdrHttp'
readonly BROKERS="192.168.1.103:9092\
,192.168.1.105:9092\
,192.168.1.106:9092\
,192.168.1.107:9092\
,192.168.1.109:9092"

readonly PARQUET_PATH='/test/parquet/jkafka'

echo "TOPICS=${TOPICS}"
echo "BROKERS=${BROKERS}"

declare opt
mode=$1; mode=${mode:-cluster}

case ${mode} in
cluster)
	opt='--master yarn --deploy-mode cluster'
	;;
local|*)
	opt='--master local'
	;;
esac

spark-submit ${opt} \
	--driver-memory 512m \
	--executor-memory 512m \
	--num-executors 1 \
	--executor-cores 3 \
	--conf spark.default.parallelism=60 \
	--conf spark.storage.memoryFraction=0.4 \
	--conf spark.shuffle.memoryFraction=0.4 \
	--conf spark.yarn.executor.memoryOverhead=1024 \
	--conf spark.yarn.driver.memoryOverhead=1024 \
	--conf spark.streaming.kafka.maxRatePerPartition=1000 \
	--conf spark.cleaner.referenceTracking.blocking=true \
	--conf spark.streaming.concurrentJobs=16 \
	--jars ${SPARK_EXT}/${JAR_SPARK_STREAMING_KAFKA} \
	--jars ${JAR_JXDR} \
	--class "jkafka.Jkafka" \
	${JAR_TARGET} \
		${BROKERS} \
		${TOPICS} \
		${PARQUET_PATH} \
	#end

#--conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=log4j.properties" \
#--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.properties" \
#--files $(pwd)/src/main/resources/log4j.properties \
#--conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file:log4j.properties" \
#--files $(pwd)/log4j.properties \
