package com.bigdata.flink.scala

import java.io

import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}
import org.apache.flink.configuration.Configuration

import scala.tools.nsc.io.File


object DistributeCacheDemo {
  def main(args: Array[String]): Unit = {

    /*val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment

    env.registerCachedFile("hdfs:///path/to/your/file", "hdfsFile")

    val config=new Configuration()

    config.setInteger("limit",2)

    val input:DataSet[String]=env.readCsvFile[String]("/path","\t","\n").withParameters(config)

    input.map(new MyMapper)

    env.execute()*/

  }
}


class MyMapper extends RichMapFunction[String,Int]{

  override def open(parameters: Configuration): Unit = {
    val myFile:io.File=getRuntimeContext.getDistributedCache.getFile("hdfsFile")

    val limit=parameters.getInteger("limit",0)

  }


  override def map(in: String): Int = {
    //use content of cached file
    0
  }
}
