import kafka.serializer.StringDecoder

import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.SparkConf

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

import org.apache.log4j.Logger
import java.io._

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

object NetFlow {
  def main(args: Array[String]) {
    val sc = new SparkConf().setAppName("NetFlow")
    val ssc = new StreamingContext(sc, Seconds(2))
    // ssc.checkpoint("checkpoint")

    // Create direct kafka stream with brokers and topics
    val topics = "xdr,xdrHttp,xdrFile"
    val brokers = "192.168.1.103:9092,192.168.1.105:9092,192.168.1.106:9092,192.168.1.107:9092,192.168.1.109:9092"
    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, String](
	"metadata.broker.list" -> brokers,
	"auto.offset.reset" -> "smallest",
	"consumer.timeout.ms" -> "2000"
    )
    val ds = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topicsSet)

    var irdd = 0
    var irecord = 0

    ds.foreachRDD { rdd =>
      var ipartition = 0
      irdd += 1

object Log {
    val log = Logger.getLogger(getClass.getName)
}
      rdd.foreachPartition { records =>
        ipartition += 1

	Log.log.info("=========================================================\n")
	Log.log.info("rdd:" + irdd + " partition:" + ipartition + "\n")
	Log.log.info(records + "\n")
/*
	records.foreach { record =>
	  // f.write(record)
	  irecord += 1
	  Log.log.info("=========================================================\n")
	  Log.log.info("rdd:" + irdd + " partition:" + ipartition + " record:" + irecord + "\n")
	  Log.log.info(record + "\n")
	}
*/
      }
    }

    ssc.start()
    ssc.awaitTermination()
  }
}

