package com.bigdata.flink.scala

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.{OverWindow, TableEnvironment, Types}
import org.apache.flink.table.sources.CsvTableSource

object SqlExplainDemo {

  def main(args: Array[String]): Unit = {

    val env:StreamExecutionEnvironment=StreamExecutionEnvironment.getExecutionEnvironment
    val tEnv=TableEnvironment.getTableEnvironment(env)
    //val csvSource=new CsvTableSource("/",Array("fileName1","filedName2"),Array(new BasicTypeInfo[String](),new BasicTypeInfo[String]()))
    val csvSource=new CsvTableSource("D:\\flink_dir\\csv.csv",
      Array("name","age","mobile"),
      Array(Types.STRING,Types.INT,Types.STRING),",","\n")
    tEnv.registerTableSource("csvSourceTable",csvSource)
    val table=tEnv.sqlQuery("select name,age from csvSourceTable")


    table.printSchema()


    env.execute()

  }

}