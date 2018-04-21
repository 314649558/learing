package com.bigdata.flink.scala.kafka

import com.bigdata.flink.scala.Constants
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer010, FlinkKafkaProducer010}


//kafka-console-producer.sh --broker-list master.com:9092 --topic input_topic
//kafka-console-consumer.sh --zookeeper master.com:2181 --topic output_topic
object FromKafkaToKafkaDemo {

  def main(args: Array[String]): Unit = {
    val prefix="testFlink"
    val env = StreamExecutionEnvironment.getExecutionEnvironment
       val inputArgs=Array("--bootstrap.servers","master.com:9092",
       "--zookeeper.connect","master.com:2181",
       "--group.id","gid001")
    val params = ParameterTool.fromArgs(inputArgs)
     env.getConfig.disableSysoutLogging()
    //env.getConfig.setRestartStrategy(RestartStrategies.fixedDelayRestart(4,10000))
    env.getConfig.setGlobalJobParameters(params)
    val kafkaConsumer=new FlinkKafkaConsumer010(Constants.INPUT_TOPIC,new SimpleStringSchema,params.getProperties)
    val messageStream=env.addSource(kafkaConsumer).map(instr=> {
      println(instr)
      s"""$prefix:$instr"""
    })

    messageStream.print().setParallelism(1)

    val kafkaProducer = new FlinkKafkaProducer010(Constants.OUTPUT_TOPIC,
      new SimpleStringSchema,
      params.getProperties)
    messageStream.addSink(kafkaProducer)
    env.execute("Kafka 0.10 Example")
  }
}
