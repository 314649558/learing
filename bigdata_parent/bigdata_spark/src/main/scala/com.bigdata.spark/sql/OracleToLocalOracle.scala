package com.bigdata.spark.sql

import java.util.UUID

import com.bigdata.spark.SparkConnectionBase

object OracleToLocalOracle extends SparkConnectionBase{
  def main(args: Array[String]): Unit = {
    val Array(srcUrl,srcUser,srcPass,srcTable,tarUrl,tarUser,tarPass,tarTable,hdfsPath)=args
    implicit val appName=this.getClass.getSimpleName
    val spark=getSparkSession
    val sql=s"""(select kehuzhao,Max(zhanghao) as zhanghao from ${srcTable} from group by kehuzhao)"""
    val tmpDF=spark.read.format("jdbc").options(getJDBCConfig(srcUrl,srcUser,srcPass,sql)).load()

    val tmpRDD=tmpDF.rdd.map(row=>{
      org.apache.spark.sql.Row(
        UUID.randomUUID().toString.replace("-",""),
        row.getRowValue("kehuzhao"),
        row.getRowValue("zhanghao")
      )
    })
    val df=spark.createDataFrame(tmpRDD,DefineSchema.schema)
    //Save Data to Oracle
    df.write.format("jdbc").options(getJDBCConfig(tarUrl,tarUser,tarPass,tarTable)).save()




    //Save Data to parquet
    df.write.parquet(hdfsPath)
  }







}
