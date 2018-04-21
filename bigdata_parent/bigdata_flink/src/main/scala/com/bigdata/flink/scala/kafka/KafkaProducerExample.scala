package com.bigdata.flink.scala.kafka

import java.util.Properties

import com.bigdata.flink.scala.Constants
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.util.Random

object KafkaProducerExample {
  def main(args: Array[String]): Unit = {
    val kafkaProducer  = new KafkaProducer[String,String](getProperties(Constants.KAFKA_BROKER_LIST))
    val times=100
    for (i <- 0 to times){
      val message=s"""producer:第${i}条消息:${System.currentTimeMillis()}"""
      val record=new ProducerRecord[String,String](Constants.INPUT_TOPIC,0,"",message)
      kafkaProducer.send(record)

    }
  }


  def getProperties(brokerList:String):Properties={
    val props=new Properties()
    props.put("bootstrap.servers", brokerList)
    props.put("acks", "0")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props
  }

}
