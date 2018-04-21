package com.bigdata.flink.scala.etldemo


import com.bigdata.flink.scala.Constants
import org.apache.flink.api.java.io.jdbc.{JDBCAppendTableSink, JDBCInputFormat, JDBCOutputFormat}
import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.table.api.{TableEnvironment, Types}
import org.apache.flink.types.Row
import org.apache.flink.api.scala._
object FromMySqlToMySqlDemo extends ETLBase {

  def main(args: Array[String]): Unit = {
    val env=ExecutionEnvironment.getExecutionEnvironment
    env.getConfig.setMaxParallelism(10)
    val rowTypeInfo=new RowTypeInfo(Types.STRING,Types.STRING)
    val tabEnv=TableEnvironment.getTableEnvironment(env)
    val userDS:DataSet[Row]=env.createInput(JDBCInputFormat
      .buildJDBCInputFormat()
      .setDrivername(Constants.MYSQL_DRIVER_NAME)
      .setDBUrl(Constants.MYSQL_URL)
      .setUsername(Constants.MYSQL_USERNAME)
      .setPassword(Constants.MYSQL_PASSWD)
      .setQuery("select cast(id as char) as id,login_name from sys_user")
      .setRowTypeInfo(rowTypeInfo)
      .finish())
    val table=tabEnv.fromDataSet(userDS)
    tabEnv.registerDataSet("user",userDS)




    tabEnv.sqlQuery("select * from `user`")
   /* userDS.output(JDBCOutputFormat.buildJDBCOutputFormat()
                .setDrivername(Constants.MYSQL_DRIVER_NAME)
                .setDBUrl(Constants.MYSQL_URL)
                .setUsername(Constants.MYSQL_USERNAME)
                .setPassword(Constants.MYSQL_PASSWD)
                .setQuery("insert into test (id,name) values(?,?)")
                .finish()).setParallelism(2)
      */
    val jdbcSink=JDBCAppendTableSink.builder()
      .setDrivername(Constants.MYSQL_DRIVER_NAME)
      .setDBUrl(Constants.MYSQL_URL)
      .setUsername(Constants.MYSQL_USERNAME)
      .setPassword(Constants.MYSQL_PASSWD)
      .setQuery("insert into test (id,name) values(?,?)").setParameterTypes(Types.STRING,Types.STRING).build()
    table.writeToSink(jdbcSink)
    env.execute()

  }

}