package jkafka

import com.byzoro.xdr.{Jxdr, Jschema}
import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object Jkafka {
    def main(args: Array[String]) {
        val brokers = args(0)
        val topics = args(1)
        val savePath = args(2)

        val conf = new SparkConf().setAppName("jkafka")
        val ssc = new StreamingContext(conf, Seconds(3))

        ssc.checkpoint("jkafka-checkpoint")

        // Create direct kafka stream with brokers and topics
        val topicsSet = topics.split(",").toSet
        val kafkaParams = Map[String, String](
            "metadata.broker.list" -> brokers,
            "auto.offset.reset" -> "smallest",
            "consumer.timeout.ms" -> "3000"
        )
        val kds = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
            ssc, kafkaParams, topicsSet)

        kds.foreachRDD { rdd =>
            val rowRdd = rdd.map(pair => Row(Jxdr(pair._2)))
            //val rowRdd = rdd.map(pair => Jxdr(pair._2))
            val sqlContext = new SQLContext(rdd.sparkContext)
            val df = sqlContext.createDataFrame(rowRdd, Jschema.root)

            df.write.mode("append").parquet(savePath)
        }

        ssc.start()
        ssc.awaitTermination()
    }
}
