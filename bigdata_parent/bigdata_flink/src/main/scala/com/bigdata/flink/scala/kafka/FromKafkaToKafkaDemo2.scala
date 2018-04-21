package com.bigdata.flink.scala.kafka

import java.util.Properties

import com.bigdata.flink.scala.Constants
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer010, FlinkKafkaProducer010, Kafka010JsonTableSource, KafkaTableSource}
import org.apache.flink.table.api.{TableEnvironment, TableSchema, Types}

object FromKafkaToKafkaDemo2 {



  def main(args: Array[String]): Unit = {


    val env:StreamExecutionEnvironment=StreamExecutionEnvironment.getExecutionEnvironment

    val tabEnv=TableEnvironment.getTableEnvironment(env)


    val produceProps=new Properties()

    produceProps.setProperty("bootstrap.servers","master.com:9092")

    val source=Kafka010JsonTableSource.builder()
      .forTopic(Constants.INPUT_TOPIC)
      .withKafkaProperties(produceProps)
      .failOnMissingField(true)
      .withSchema(TableSchema.builder()
        .field("id",Types.INT)
        .field("name",Types.STRING).build()).build()


    val table=tabEnv.registerTableSource("kafka",source)






  }

}
