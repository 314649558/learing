package com.sxd.citic.drools.sink

import java.net.InetSocketAddress
import java.sql.{Connection, PreparedStatement}
import java.util
import java.util.UUID

import com.google.gson.JsonObject
import com.sxd.citic.drools.core.bean.FieldMappingBean
import com.sxd.citic.drools.core.utils.{Constants, JsonUtils}
import com.sxd.citic.drools.utils.elasticsearch.{ESDruidUtils, ESUtils}
import com.sxd.citic.drools.utils.http.HttpUtils
import com.sxd.citic.drools.utils.thread.RestRequestRunnable
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction
import org.apache.flink.streaming.connectors.elasticsearch5.ElasticsearchSink
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.elasticsearch.client.transport.TransportClient

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

/**
  * Created by Administrator on 2018/8/6.
  *
  * 自定义Sink  数据发送到 ES后，在做统计 统计后的结果发送到KAFKA
  *
  */
class ESKafkaRealTimeSink(userConfig: Map[String, String],
                          transportAddresses: util.List[InetSocketAddress],
                          elasticsearchSinkFunction: ElasticsearchSinkFunction[String])
                          extends ElasticsearchSink[String](userConfig: Map[String, String],
                                                                transportAddresses: util.List[InetSocketAddress],
                                                                elasticsearchSinkFunction: ElasticsearchSinkFunction[String]){
  var kafkaTopic:String=_
  var kafkaConfig:Map[String,String]=_
  var producer:KafkaProducer[String,String]=_
  var esUrl:String=_
  var conn:Connection=_
  var esClient:TransportClient=_
  var httpClient:CloseableHttpClient=_
  var channel:ArrayBuffer[FieldMappingBean]=_


  def this(userConfig: Map[String, String],
           transportAddresses: util.List[InetSocketAddress],
           elasticsearchSinkFunction: ElasticsearchSinkFunction[String],
           channel:ArrayBuffer[FieldMappingBean]){
    this(userConfig,transportAddresses,elasticsearchSinkFunction)
    this.kafkaTopic=kafkaTopic
    this.kafkaConfig=kafkaConfig
    this.channel=channel
    this.esUrl=s"jdbc:elasticsearch://${Constants.ES_ADDR.split(",")(0)}/${Constants.ES_INDEX_NAME_CHL_TMP}"
  }
  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    ESDruidUtils.getDruidDataSource(this.esUrl)
    this.conn=ESDruidUtils.getDruidDataSource(this.esUrl)
    this.esClient=ESUtils.getTransportClient()
    this.httpClient=HttpUtils.getHttpClient(Constants.REST_URL).get
  }
  override def invoke(value: String): Unit = {
    super.invoke(value)
    //TODO ES  SQL语句应该来源于数据库，并做好字段配置
    implicit val conn = this.conn
    var pstmt:PreparedStatement= ESDruidUtils.getPreparedStatement(Constants.ES_AGGRS_SQL)
    try {
      val rs = pstmt.executeQuery()
      val lst :util.List[JsonObject]= new util.ArrayList[JsonObject]()
      val bid=UUID.randomUUID().toString.replaceAll("-","")
      while (rs.next()) {
        val jsonObject = new JsonObject
        jsonObject.addProperty("bid",bid)
        jsonObject.addProperty("bthId",rs.getString("bthId"))
        jsonObject.addProperty("tradeDate", rs.getString("tradeDate"))
        jsonObject.addProperty("deptNbr", rs.getString("deptNbr"))
        jsonObject.addProperty("storeNbr", rs.getString("storeNbr"))
        jsonObject.addProperty("itemNbr", rs.getString("itemNbr"))
        jsonObject.addProperty("totalCostAmt", rs.getDouble("totalCostAmt"))
        jsonObject.addProperty("totalRetailAmt", rs.getDouble("totalRetailAmt"))
        lst.add(jsonObject)
      }

      ESUtils.writeDataES(esClient,lst,Constants.ES_INDEX_NAME_CHL,"app_stm_001_cnl")

      //sendRestApi(bid,lst)

    }catch{
      case ex:Exception=>{
        ex.printStackTrace()
      }
    }finally{
      ESDruidUtils.closePstmt(pstmt)
    }
  }

  private def sendRestApi(bid:String,dataLst: util.List[JsonObject]) = {

    val mapData = new util.HashMap[String, util.List[JsonObject]]()
    for (jsonObj <- dataLst) {
      val bthId = jsonObj.get("bthId").getAsString
      val lstData = mapData.get(bthId)
      if (lstData != null) {
        lstData.add(jsonObj)
      } else {
        val tmpLst = new util.ArrayList[JsonObject]()
        tmpLst.add(jsonObj)
        mapData.put(bthId, tmpLst)
      }
    }
    val keySet = mapData.keySet()
    for (bthId <- keySet) {
      val lst = mapData.get(bthId)
      val headObj = new JsonObject
      headObj.addProperty("appId", "1")
      headObj.addProperty("bid", bid)
      headObj.addProperty("bthId", bthId)
      headObj.addProperty("timestamp", System.currentTimeMillis())
      val resultMap = new JsonObject
      resultMap.add("head", headObj)
      resultMap.add("data", JsonUtils.strToJsonArray(JsonUtils.toJson(lst)))
      new Thread(new RestRequestRunnable(httpClient,Constants.REST_URL,JsonUtils.toJson(resultMap))).start()

    }
  }



  override def close(): Unit = {
    super.close()
    ESDruidUtils.closeConnection(this.conn)
    ESUtils.close(this.esClient)
    HttpUtils.close(httpClient)
  }
  /**
    * 数据发送kafka
    * @param value
    */
  private def sendKafka(value:String):Unit={
    val record=new ProducerRecord[String, String](this.kafkaTopic, "", value)
    this.producer.send(record)
  }





}
