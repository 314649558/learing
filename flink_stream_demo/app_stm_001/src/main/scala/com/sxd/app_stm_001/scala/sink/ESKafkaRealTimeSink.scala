package com.sxd.app_stm_001.scala.sink

import java.net.InetSocketAddress
import java.sql.{Connection, PreparedStatement}
import java.util
import java.util.UUID
import java.util.concurrent.Executors

import com.google.gson.JsonObject
import com.sxd.app_stm_001.scala.utils.elasticsearch.{ESDruidUtils, ESUtils}
import com.sxd.app_stm_001.scala.utils.thread.RestRequestRunnable
import com.sxd.app_stm_001.scala.utils.{Constants, JsonUtils}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction
import org.apache.flink.streaming.connectors.elasticsearch5.ElasticsearchSink
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.elasticsearch.action.support.WriteRequest
import org.elasticsearch.client.transport.TransportClient
import org.springframework.http.{HttpEntity, HttpHeaders, MediaType}
import org.springframework.web.client.RestTemplate

import scala.collection.JavaConversions._
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
  var restTemplate:RestTemplate=_
  var esClient:TransportClient=_



  //val executeThreadPool=Executors.newFixedThreadPool(10)

  def this(userConfig: Map[String, String],
           transportAddresses: util.List[InetSocketAddress],
           elasticsearchSinkFunction: ElasticsearchSinkFunction[String],
           kafkaTopic:String,
           kafkaConfig:Map[String,String]){
    this(userConfig,transportAddresses,elasticsearchSinkFunction)
    this.kafkaTopic=kafkaTopic
    this.kafkaConfig=kafkaConfig
    this.esUrl=s"jdbc:elasticsearch://${Constants.ES_ADDR.split(",")(0)}/${Constants.ES_INDEX_NAME_CHL_TMP}"
  }
  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    //this.producer=new KafkaProducer[String,String](this.kafkaConfig)
    ESDruidUtils.getDruidDataSource(this.esUrl)
    this.conn=ESDruidUtils.getDruidDataSource(this.esUrl)
    this.restTemplate=new RestTemplate()
    this.esClient=ESUtils.getTransportClient()
  }
  override def invoke(value: String): Unit = {
    super.invoke(value)
    //TODO ES  SQL语句应该来源于数据库，并做好字段配置
    implicit val conn = this.conn
    var pstmt:PreparedStatement= ESDruidUtils.getPreparedStatement(Constants.ES_AGGRS_SQL)
    try {
      val rs = pstmt.executeQuery()
      val lst :util.List[JsonObject]= new util.ArrayList[JsonObject]()
      //ESUtils.deleteIndex(this.esClient,Constants.ES_INDEX_NAME_CHL_TMP)
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

    val mapData=new util.HashMap[String,util.List[JsonObject]]()
    for(jsonObj<-dataLst){
      val bthId=jsonObj.get("bthId").getAsString
      val lstData=mapData.get(bthId)
      if(lstData!=null){
        lstData.add(jsonObj)
      }else{
        val tmpLst=new util.ArrayList[JsonObject]()
        tmpLst.add(jsonObj)
        mapData.put(bthId,tmpLst)
      }
    }
    val keySet=mapData.keySet()
    for(bthId<-keySet){
      val lst=mapData.get(bthId)
      val headObj=new JsonObject
      headObj.addProperty("appId","1")
      headObj.addProperty("bid",bid)
      headObj.addProperty("bthId",bthId)
      headObj.addProperty("timestamp",System.currentTimeMillis())
      val resultMap=new JsonObject
      resultMap.add("head",headObj)
      resultMap.add("data",JsonUtils.strToJsonArray(JsonUtils.toJson(lst)))
      val httpHeaders: HttpHeaders = new HttpHeaders
      val mediaType = MediaType.parseMediaType("application/json; charset=UTF-8")
      httpHeaders.setContentType(mediaType)
      httpHeaders.add("Accept", MediaType.APPLICATION_JSON.toString)
      val formEntity = new HttpEntity(JsonUtils.toJson(resultMap), httpHeaders)

     /* try {
        restTemplate.postForObject(Constants.REST_URL, formEntity, classOf[String])
      }catch{
        case ex:Exception => ex.printStackTrace()
    }*/
      new Thread(new RestRequestRunnable(restTemplate,formEntity)).start()

    }
  }
  override def close(): Unit = {
    super.close()
    //关闭kafka的生产者
   /* if(producer!=null ){
      producer.close()
    }*/
    ESDruidUtils.closeConnection(this.conn)
    ESUtils.close(this.esClient)
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
