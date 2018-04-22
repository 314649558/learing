package com.bigdata.spark.streaming

import java.util.concurrent.LinkedBlockingQueue

import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}

object DStreamDemo2 {

  def main(args: Array[String]): Unit = {



    val spark = SparkSession
      .builder
      .appName(this.getClass.getName)
      //  .config("spark.sql.streaming.metricsEnabled",true)
      .master("local[3]")
      .getOrCreate()
    val ssc=new StreamingContext(spark.sparkContext,Seconds(2))

    //购买数据
    //{cid,buy:原始数据}
    val lines1=ssc.socketTextStream("192.168.112.101",9999,StorageLevel.MEMORY_ONLY).filter(_.length>=3).map((_,s"""buy:${_}"""))


    //交易数据
    //（cid，trans:原始数据）
    val lines2=ssc.socketTextStream("192.168.112.101",9999,StorageLevel.MEMORY_ONLY).filter(_.length>=3).map((_,s"""tra:${_}"""))

    lines1.union(lines2).foreachRDD(rdd=>{
     rdd.groupBy(_._2).map(_._2).filter(_.size>2).foreachPartition(it=>{
       it.foreach(dataIt=>{
         var flag=false
         dataIt.map(_._2).foreach(row=>{
           if(row.startsWith("tra:")){
              flag=true
           }
         })
       })

     })
    })


    ssc.start()
    ssc.awaitTermination()



  }

}
