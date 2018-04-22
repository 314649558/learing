package com.bigdata.spark.streaming

import java.util.concurrent.LinkedBlockingQueue

import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Duration, Minutes, Seconds, StreamingContext}

object DStreamDemo {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName(this.getClass.getName)
      //  .config("spark.sql.streaming.metricsEnabled",true)
      .master("local[3]")
      .getOrCreate()
    val ssc=new StreamingContext(spark.sparkContext,Seconds(2))


    val lines=ssc.socketTextStream("192.168.112.101",9999,StorageLevel.MEMORY_ONLY)


    val words = lines.flatMap(_.split(" "))
    println(words.count())
    ssc.start()
    ssc.awaitTermination()
   /* val d1=lines.flatMap(_.split(" ")).map((_,1))
    val d2=lines.flatMap(_.split(" ")).map((_,1))
    d1.join(d2)*/
  }

}
