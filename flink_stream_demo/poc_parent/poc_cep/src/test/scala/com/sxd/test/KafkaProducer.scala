package com.sxd.test

import java.util.concurrent.TimeUnit

import com.google.gson.JsonArray
import com.sxd.citic.cep.core.utils.JsonUtils
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.collection.JavaConversions.mapAsJavaMap
import scala.util.Random
/**
  * Created by Administrator on 2018/8/1.
  */
object KafkaProducer {
  def main(args: Array[String]): Unit = {


    val topic="pocsrc2"
    //val topic="test13"

    val kafkaParams = Map(
      "bootstrap.servers" -> "192.168.112.101:9092",
      //"bootstrap.servers" -> "192.168.132.215:6667,192.168.132.216:6667,192.168.132.217:6667",
      "key.serializer" -> "org.apache.kafka.common.serialization.StringSerializer",
      "value.serializer" -> "org.apache.kafka.common.serialization.StringSerializer"
    )
    val producer = new KafkaProducer[String, String](kafkaParams)


    val dateLst=List("20180701",
      "20180702",
      "20180703",
      "20180704",
      "20180705",
      "20180706",
      "20180707",
      "20180708",
      "20180709",
      "20180710",
      "20180711",
      "20180712",
      "20180713",
      "20180714",
      "20180715",
      "20180716",
      "20180717",
      "20180718",
      "20180719",
      "20180720",
      "20180721",
      "20180722",
      "20180723",
      "20180724",
      "20180725",
      "20180726",
      "20180727",
      "20180728",
      "20180729",
      "20180730",
      "20180731",
      "20180801",
      "20180802",
      "20180803",
      "20180804",
      "20180805",
      "20180806",
      "20180807",
      "20180808",
      "20180809"
    )




    while(true){
      val jsonArray=new JsonArray
      for(i <- 0 until Random.nextInt(10)+1){
        val bean=ItemBean(
          dateLst(Random.nextInt(dateLst.size)),
          Random.nextInt(Integer.MAX_VALUE).toString,
          Random.nextInt(100).toString,
          Random.nextInt(100).toString,

          Random.nextDouble()*100,
          Random.nextDouble()*100,
          Random.nextDouble()*1000
        )
        val bean2=ItemBean2(
          Random.nextInt(Integer.MAX_VALUE).toString,
          Random.nextInt(100).toString,
          Random.nextInt(100).toString,
          Random.nextDouble()*100,
          Random.nextDouble()*100,
          Random.nextDouble()*1000
        )
        val jsonObj=JsonUtils.strToJson(JsonUtils.toJson(bean))
        val jsonObj2=JsonUtils.strToJson(JsonUtils.toJson(bean2))
        jsonArray.add(jsonObj)
        jsonArray.add(jsonObj2)
      }

      val value=jsonArray.toString
      println(value)
      val record=new ProducerRecord[String, String](topic, "", value)
      val f=producer.send(record)
      val recordMetadata=f.get(1,TimeUnit.SECONDS)
      println(recordMetadata)
      Thread.sleep(3000)
    }
  }
}


case class ItemBean(tradeDate:String,deptNbr:String,storeNbr:String,itemNbr:String,costAmt:Double,netSalesAmt:Double,retailAmt:Double)
case class ItemBean2(deptNbr:String,storeNbr:String,itemNbr:String,costAmt:Double,netSalesAmt:Double,retailAmt:Double)
