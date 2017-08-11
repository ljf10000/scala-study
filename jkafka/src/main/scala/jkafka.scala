/**
  * Created by l_j_f on 2017-07-24.
  */

import kafka.serializer.StringDecoder

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SQLContext}

import jxdr.Jxdr

/*
object Log extends Serializable {
//   @transient lazy val log = Logger.getLogger(getClass.getName)
     var log: PrintWriter = _

     def logger():PrintWriter = {
	synchronized{
	     if(log==null) {
		  log = new PrintWriter(new File("/tmp/liujf-file-netflow"))
	     }
   	}

	log
     }
}
*/

/*
object Log extends Serializable{
    @transient lazy val log = Logger.getLogger(getClass.getName)
}
*/

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
            val sqlContext = new SQLContext(rdd.sparkContext)
            val rowRdd = rdd.map(pair => Row(Jxdr(pair._2)))
            //val rowRdd = rdd.map(pair => Jxdr(pair._2))
            val df = sqlContext.createDataFrame(rowRdd, Jxdr.SCHEMA)

            df.write.parquet("./parquet")
        }

        ssc.start()
        ssc.awaitTermination()
    }
}


