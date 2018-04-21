package com.bigdata.spark.sql

import com.bigdata.spark.SparkConnectionBase
import org.apache.hadoop.io._
import org.apache.hadoop.mapred.TextInputFormat

object FromHDFSToLocal extends SparkConnectionBase{

  def main(args: Array[String]): Unit = {
/*

    implicit val appName=this.getClass.getSimpleName
    val spark=getSparkSession
    val text=spark.sparkContext.hadoopFile[LongWritable,Text]("",
      classOf[TextInputFormat[LongWritable,Text]],
      classOf[LongWritable],
      classOf[Text])

*/


  }

}
