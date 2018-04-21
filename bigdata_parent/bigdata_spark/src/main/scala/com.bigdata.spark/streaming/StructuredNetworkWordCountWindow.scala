package com.bigdata.spark.streaming

import org.apache.spark.sql.SparkSession
import java.sql.Timestamp
import java.util.concurrent.{ArrayBlockingQueue, LinkedBlockingQueue}

import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.OutputMode

object StructuredNetworkWordCountWindow {

def main(args: Array[String]): Unit = {



  val spark = SparkSession
    .builder
      .master("local[1]")
    .appName(this.getClass.getName)
    .getOrCreate()


  import spark.implicits._

  val lines = spark.readStream
    .format("socket")
    .option("host", "192.168.112.101")
    .option("port", 9999)
    .option("includeTimestamp", true)
    .load()


  val lines2 = spark.readStream
    .format("socket")
    .option("host", "192.168.112.101")
    .option("port", 9999)
    .option("includeTimestamp", true)
    .load()


  val l=lines.union(lines2)



  val windowSize=4

  val slideSize=2

  val windowDuration = s"$windowSize seconds"
  val slideDuration = s"$slideSize seconds"

  val words=lines.as[(String,Timestamp)].flatMap(line=>line._1.split(" ").map((_,line._1))).toDF("word","timestamp")




  val windowCounts=words
        //.withWatermark("timestamp","20 seconds")
    .groupBy(window($"timestamp",windowDuration,slideDuration),$"word")
    .count()
    .orderBy("window")







  val query=windowCounts.
    writeStream.
    outputMode("complete").
    format("console").
    queryName("StructuredNetworkWordCountWindow").
    option("truncate",false).start()

  query.awaitTermination()

}

}
