package com.bigdata.spark.streaming

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.OutputMode

  object StructuredNetworkWordCount {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder
      .appName(this.getClass.getName)
      //  .config("spark.sql.streaming.metricsEnabled",true)
      //  .master("local[1]")
      .getOrCreate()


    import spark.implicits._

    val linesDF=spark.readStream.format("socket")
        .option("host","192.168.112.101")
      .option("port",9999)
        .option("spark.streaming.blockInterval",50)
      .load()



    val words = linesDF.as[String].flatMap(_.split(" "))



    val wordCounts =words.groupBy("value").count()


    val query=wordCounts.writeStream.outputMode("complete").format("console").start()

    Thread.sleep(10000)

    println("query.lastProgress")
    println(query.lastProgress)

    println("query.status")
    println(query.status)


    query.awaitTermination()





  }

}
