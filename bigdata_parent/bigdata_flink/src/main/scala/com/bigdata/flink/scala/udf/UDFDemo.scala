package com.bigdata.flink.scala.udf

import org.apache.flink.api.scala._
import org.apache.flink.core.fs.FileSystem.WriteMode
import org.apache.flink.table.api.{TableEnvironment, Types}
import org.apache.flink.table.sinks.CsvTableSink
import org.apache.flink.table.sources.CsvTableSource

object UDFDemo {
  def main(args: Array[String]): Unit = {

    val env=ExecutionEnvironment.getExecutionEnvironment

    val tabEnv=TableEnvironment.getTableEnvironment(env)

    val csvSource=new CsvTableSource("D:\\flink_dir\\csv.csv",
      Array("name","age","mobile"),
      Array(Types.STRING,Types.STRING,Types.STRING),",","\n")


    tabEnv.registerFunction("split",new Split("#"))

    tabEnv.registerTableSource("csvSourceTable",csvSource)

    //val table=tabEnv.sqlQuery("select name,age,mobile from csvSourceTable,LATERAL TABLE(split(name)) as T(word,length)")
    val table=tabEnv.sqlQuery("select name,word,length from csvSourceTable LEFT JOIN TABLE(split(name)) as T(word,length) ON TRUE")
    //val table=tabEnv.sqlQuery("select name,age,mobile from csvSourceTable")

    table.printSchema()

    val tableSink=new CsvTableSink("D:\\flink_dir\\target",Some(","),None,Some(WriteMode.OVERWRITE))
    table.writeToSink(tableSink)
    env.execute()


  }


}
