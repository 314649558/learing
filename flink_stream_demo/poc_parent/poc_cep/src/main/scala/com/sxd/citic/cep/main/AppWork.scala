package com.sxd.citic.cep.main

import java.net.InetSocketAddress
import java.util._
import java.{lang, util}

import com.google.gson.{JsonArray, JsonObject}
import com.sxd.citic.cep.core.bean.RuleExpressionBean
import com.sxd.citic.cep.core.common.QueryMysqlUtils
import com.sxd.citic.cep.core.utils.{Constants, JsonUtils}
import com.sxd.citic.cep.function._
import com.sxd.citic.cep.sink.ESKafkaRealTimeSink
import com.sxd.citic.cep.utils.ExpressionUtils
import org.apache.commons.lang3.StringUtils
import org.apache.flink.api.common.functions.{FlatMapFunction, MapFunction, RichMapFunction}
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.cep.scala.CEP
import org.apache.flink.cep.scala.pattern.Pattern
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.collector.selector.OutputSelector
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.elasticsearch5.ElasticsearchSink
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.apache.flink.util.Collector

import scala.util.control.Breaks._

/**
  * Created by Administrator on 2018/8/13.
  */
object AppWork {

  def main(args: Array[String]): Unit = {
    val paramTool: ParameterTool =ParameterTool.fromArgs(args)
    val app_id=paramTool.get("appid","2")
    val app= QueryMysqlUtils.getAPP(app_id)
    val source = QueryMysqlUtils.getSrcField(app.dsId)
    val channel = QueryMysqlUtils.getChlField(app.cnId)


    val appCeps=QueryMysqlUtils.getAppCep(app_id)
    val rules: util.List[RuleExpressionBean] = new util.ArrayList[RuleExpressionBean]()
    for(i <- 0 until appCeps.size){
      rules.add(new RuleExpressionBean(appCeps(i).fieldName,appCeps(i).expression))
    }
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(4)
    env.getConfig.disableSysoutLogging
    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)
    env.enableCheckpointing(2000)
    val kafkaSrcProp=new Properties()
    kafkaSrcProp.put(Constants.BOOTSTRAP_SERVERS_KEY,app.srcKafkaHosts)
    kafkaSrcProp.put(Constants.MAX_REQUEST_SIZE_KEY,Constants.MAX_REQUEST_SIZE_VALUE)
    kafkaSrcProp.put(Constants.MAX_PARTITION_FETCH_BYTES_KEY,Constants.MAX_PARTITION_FETCH_BYTES_VALUE)
    kafkaSrcProp.put(Constants.ZOOKEEPER_CONNECT_KEY,app.srcZookeeperHosts)
    kafkaSrcProp.put(Constants.GROUP_ID_KEY,app.srcGroups)
    val kafkaConsumer = new FlinkKafkaConsumer010(app.srcTopic,new SimpleStringSchema(),kafkaSrcProp)


    val kafkaMessage=env.addSource(kafkaConsumer)
    /**
      * 原始数据 添加bthid 和head
      */
    val splitStream: SplitStream[String] =kafkaMessage.filter(x=>{
      try
        JsonUtils.strToJsonArray(x).size() > 0
      catch {
        case ex: Exception =>
          false
      }
    }).map(bthData=>{
      val retLst = new util.ArrayList[String]

      val jsonObject = new JsonObject
      val bthId = UUID.randomUUID.toString.replaceAll("-", "")
      jsonObject.addProperty("bthId", bthId)
      jsonObject.addProperty("timestamp", System.currentTimeMillis)
      jsonObject.addProperty("appId", app_id)
      jsonObject.addProperty("appName", app.appName)
      val jsonArray = JsonUtils.strToJsonArray(bthData)
      val tmpJsonArray = new JsonArray

      for(i <- 0 until jsonArray.size){
        val jsonObj = JsonUtils.strToJson(jsonArray.get(i).toString)
        jsonObj.addProperty("bthId", bthId)
        jsonObj.addProperty("id", UUID.randomUUID.toString.replaceAll("-", ""))
        tmpJsonArray.add(jsonObj)
      }
      retLst.add(JsonUtils.toJson(jsonObject)) //添加头部信息
      retLst.add(JsonUtils.toJson(tmpJsonArray))
      retLst
    }).flatMap(new FlatMapFunction[util.ArrayList[String], String] {
      override def flatMap(value: util.ArrayList[String], out: Collector[String]): Unit = {
        for(i <- 0 until value.size()){
          out.collect(value.get(i))
        }
      }
    }).split(new OutputSelector[String](){
      override def select(value: String): lang.Iterable[String] = {
        val lst = new util.ArrayList[String]

        if(StringUtils.contains(value,"appName")) lst.add("head") else lst.add("orginData")
        lst
      }
    })


    /*******  ES 配置***************/
    val esConfig = new util.HashMap[String, String]
    esConfig.put(Constants.ES_CLUSTER_NAME_KEY, Constants.ES_CLUSTER_NAME_VALUE)
    esConfig.put("bulk.flush.max.actions", "100")
    val transportAddresses = new util.ArrayList[InetSocketAddress]
    val addrs: Array[String] =Constants.ES_ADDR.split(",")
    for(addr <-addrs){
      transportAddresses.add(new InetSocketAddress(addr.split(":")(0), addr.split(":")(1).toInt))
    }


    //头部数据发送ES
    val headStream = splitStream.select("head")
    val headConfig = new util.HashMap[String, String]
    headConfig.put(Constants.ES_INDEX_KEY, "idx_app_data_batch_")
    headConfig.put(Constants.ES_TYPE_KEY, "app_data_batch_")
    headStream.addSink(new ElasticsearchSink[String](esConfig, transportAddresses, new POCElasticsearchSinkFunction(headConfig)))


    //原始数据->之后都应该基于这个数据做处理
    val orginStream: DataStream[String] = splitStream.select("orginData")


    //发送原始数据到ES
    val orginConfig = new util.HashMap[String, String]
    orginConfig.put(Constants.ES_INDEX_KEY, "idx_app_stm_002_ods")
    orginConfig.put(Constants.ES_TYPE_KEY, "app_stm_002_ods")


    //原始数据发送ES
    orginStream.map(new RichMapFunction[String, util.List[String]]() {
      override def map(value: String): util.List[String] = {
        val lst = new util.ArrayList[String]
        val jsonArr = JsonUtils.strToJsonArray(value)
        for(i <- 0 until jsonArr.size()){
          lst.add(jsonArr.get(i).toString)
        }
        lst
      }
    }).flatMap(new FlatMapFunction[util.List[String], String] {
      override def flatMap(value: util.List[String], out: Collector[String]): Unit = {
        for(i<- 0 until value.size()){
          out.collect(value.get(i))
        }
      }
    }).addSink(new ElasticsearchSink[String](esConfig, transportAddresses, new POCElasticsearchSinkFunction(orginConfig)));



    val p=Pattern.begin[String]("item_first").where(data=>{
      def checkMustField :Boolean= {
        var flag = true
        breakable(
          for (field <- source) {
            if (!StringUtils.contains(data, field.fieldName)) {
              //记录日志 并将数据暂时发送到KAFKA 便于数据审查
              // TODO send kafka and write log
              flag = false
              break()
            }
          }
        )
        flag
      }
      checkMustField
    })


    val cepPatternDS: DataStream[String] =CEP.pattern(orginStream,p).select(pattern=>{
      val jsonStr = pattern.get("item_first").get.toList(0)
      val jsonArr = JsonUtils.strToJsonArray(jsonStr)
      val retJsonObj = new JsonObject
      val jsonData = new JsonArray

      //异常数据
      val errorJsonData = new JsonArray

      for(k <- 0 until jsonArr.size()){
        val jsonTmpStr = jsonArr.get(k).toString
        var flag = true

        breakable(
          for(fieldMappingBean <-source){
            val tmpJsonObj1 = JsonUtils.strToJson(jsonTmpStr)

            try {
              if (!StringUtils.contains(jsonTmpStr, fieldMappingBean.fieldName)) {
                flag = false
                tmpJsonObj1.addProperty("errorInfo", " 数据中必须包含[" + fieldMappingBean.fieldName + "] 字段")
                errorJsonData.add(tmpJsonObj1)
                break()
              }
            }catch {
              case ex:Exception=>{
                break()
              }
            }
          }
        )
        if (flag) {
          val jsonObj = JsonUtils.strToJson(jsonTmpStr)
          val tmpJsonObj = new JsonObject
          tmpJsonObj.addProperty("bthId", jsonObj.get("bthId").getAsString)
          tmpJsonObj.addProperty("id", jsonObj.get("id").getAsString)

          for (fieldMappingBean <- channel) {
            try
              tmpJsonObj.addProperty(fieldMappingBean.fieldName, jsonObj.get(fieldMappingBean.fieldName).getAsString)
            catch {
              case ex: Exception => {
                tmpJsonObj.addProperty(fieldMappingBean.fieldName, 0)
                if (rules != null && rules.size > 0) {
                  for ( j <- 0 until rules.size()) {
                    if(StringUtils.equals(fieldMappingBean.fieldName,rules.get(j).filedName)){
                      var result = 0.0
                      try
                        result=ExpressionUtils.expressionParse(rules.get(j).expression, jsonObj).toDouble
                      catch {
                        case e: Exception =>

                      }
                      tmpJsonObj.addProperty(rules.get(j).filedName ,result)
                    }
                  }
                }
              }
            }
          }
          val jsonObject = JsonUtils.strToJson(JsonUtils.toJson(tmpJsonObj))
          jsonObject.addProperty("errorInfo", "")
          errorJsonData.add(jsonObject)
          jsonData.add(tmpJsonObj)
        }
      }
      retJsonObj.add("data", jsonData)
      retJsonObj.add("errorData", errorJsonData)
      JsonUtils.toJson(retJsonObj)
    })

    //对流进行切分
    val dataStream: SplitStream[String] = cepPatternDS.map(new CepMapFunction).flatMap(new CEPFlatmapFunction).split(new CepOutputSelector)

    // 3.中间数据 异常数据  写入ES
    val middleStream = dataStream.select("errorData").map(new CepErrorMapFunction).flatMap(new CEPFlatmapFunction)
    val midConfig = new util.HashMap[String, String]
    midConfig.put(Constants.ES_INDEX_KEY, "idx_app_stm_002_mid")
    midConfig.put(Constants.ES_TYPE_KEY, "app_stm_002_mid")
    middleStream.addSink(new ElasticsearchSink[String](esConfig, transportAddresses, new POCElasticsearchSinkFunction(midConfig)))


    //最终输出流
    val finalData: DataStream[String] = dataStream.select("finalData").map(new MapFunction[String,String](){
      override def map(value: String): String = {

        try{
          val jsonArr = JsonUtils.strToJsonArray(value)
          val head = new JsonObject
          val retJsonObj = new JsonObject
          val bthId = jsonArr.get(0).getAsJsonObject.get("bthId").getAsString
          head.addProperty("appId", app_id)
          head.addProperty("bthId", bthId)
          head.addProperty("consume", 0)
          head.addProperty("status", 0)
          head.addProperty("timestamp", System.currentTimeMillis)
          head.addProperty("produce", jsonArr.size())

          val jsonArrData = new JsonArray
          for (i <- 0 until jsonArr.size()) {
            val obj = jsonArr.get(i).getAsJsonObject
            jsonArrData.add(obj)
          }

          retJsonObj.add("data", jsonArrData)
          retJsonObj.add("head", head)
          JsonUtils.toJson(retJsonObj);
        }catch{
          case ex:Exception=>""
        }
      }
    }).filter((x: String) => StringUtils.isNotEmpty(x))


    val kafkaChlProp = new Properties
    kafkaChlProp.put("bootstrap.servers", app.chlKafkatHosts)
    kafkaChlProp.put("zookeeper.connect",app.chlZookeeperHosts)

    val hashMap = new util.HashMap[String, String]
    hashMap.put("bootstrap.servers", app.chlKafkatHosts)
    hashMap.put("zookeeper.connect", app.chlZookeeperHosts)
    hashMap.put("topic", app.chlTopic)
    hashMap.put(Constants.ES_INDEX_KEY, "idx_app_stm_002_cnl")
    hashMap.put(Constants.ES_TYPE_KEY, "app_stm_002_cnl")

    finalData.addSink(new ESKafkaRealTimeSink(hashMap))

    try
      env.execute(app.appName)
    catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }


}
