package com.bigdata.spark

import com.bigdata.spark.RichConvert.{RichRow, RichString}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Row
abstract class SparkConnectionBase {

  implicit def str2RichString(self:String)=new RichString(self)

  implicit def row2RichRow(self:Row)=new RichRow(self)


  def getSparkSession(implicit appName:String): SparkSession ={

    val sparkConf=new SparkConf()
    sparkConf.set("spark.serializer","org.apache.spark.serializer.KryoSerializer")
    //解决oracle 连接后长时间没响应Oracle服务器主动关闭连接的异常
    sparkConf.set("spark.executor.extraJavaOptions","-Djava.security.egd=file:/dev/../dev/urandom")
    if(System.getProperty("os.name").startsWith("linux")){
      SparkSession.builder()
          .appName(appName)
            .config(sparkConf)
          .getOrCreate()
    }else{
      SparkSession.builder()
        .appName(appName)
        .config(sparkConf)
          .master("local[2]")
        .getOrCreate()
    }
  }


  def getJDBCConfig(url:String,user:String,pass:String,table:String):Map[String,String]={

    Map("url"->url,
      "dbtable"->table,
      "user"->user,
      "password"->pass,
      "driver"->"org.jdbc.driver.OracleDriver",
      "batchsize"->"2000"
    )

  }


}
