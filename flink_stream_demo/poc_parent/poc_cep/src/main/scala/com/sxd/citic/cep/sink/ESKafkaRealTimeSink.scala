package com.sxd.citic.cep.sink

import java.util

import com.google.gson.JsonObject
import com.sxd.citic.cep.core.utils.{Constants, JsonUtils}
import com.sxd.citic.cep.utils.elasticsearch.ESUtils
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.elasticsearch.client.transport.TransportClient

import scala.collection.JavaConversions._

/**
  * Created by Administrator on 2018/8/6.
  *
  * 自定义Sink  数据发送到 ES后，在做统计 统计后的结果发送到KAFKA
  *
  */
class ESKafkaRealTimeSink(config:util.HashMap[String,String]) extends RichSinkFunction[String]{
  var kafkaProducer:KafkaProducer[String,String]= _
  var esClient:TransportClient=_
  var kafkaTopic:String=_
  var esIndexName:String=_
  var esTypeName:String=_

  override def open(parameters: Configuration): Unit = {

    val kafkaParams = Map(
      Constants.BOOTSTRAP_SERVERS_KEY -> config.get(Constants.BOOTSTRAP_SERVERS_KEY),
      "key.serializer" -> Constants.serializer_value,
      "value.serializer"-> Constants.serializer_value,
      Constants.MAX_REQUEST_SIZE_KEY->Constants.MAX_REQUEST_SIZE_VALUE,
      Constants.MAX_PARTITION_FETCH_BYTES_KEY->Constants.MAX_PARTITION_FETCH_BYTES_VALUE
    )


    this.esIndexName=config.get(Constants.ES_INDEX_KEY)
    this.esTypeName=config.get(Constants.ES_TYPE_KEY)
    this.kafkaTopic=config.get("topic")

    this.kafkaProducer=new KafkaProducer[String,String](kafkaParams)
    this.esClient=ESUtils.getTransportClient()
  }

  override def invoke(value: String, context: SinkFunction.Context[_]): Unit = {
    val  lst:util.List[JsonObject]=new util.ArrayList[JsonObject]()
    try {
      val jsonArray = JsonUtils.strToJsonArray(JsonUtils.strToJson(value).get("data").toString())

      if(jsonArray!=null && jsonArray.size()>0) {
        for (i <- 0 until jsonArray.size()) {
          lst.add(jsonArray.get(i).getAsJsonObject)
        }
        ESUtils.writeDataES(this.esClient, lst, this.esIndexName, this.esTypeName)
        sendKafka(value)
      }
    }catch{
      case ex:Exception=>
    }
  }
  override def close(): Unit = {
    super.close()
    if(kafkaProducer!=null){
      kafkaProducer.close()
    }
    if(this.esClient!=null){
      esClient.close()
    }
  }

  private def sendKafka(value:String):Unit={
    val record=new ProducerRecord[String, String](this.kafkaTopic, "", value)
    this.kafkaProducer.send(record)
  }

}
