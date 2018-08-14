package com.sxd.app_stm_001.scala.main

import java.net.InetSocketAddress
import java.util.{Properties, UUID}
import java.{lang, util}

import com.google.gson.{JsonArray, JsonObject}
import com.sxd.app_stm_001.app.entity.App
import com.sxd.app_stm_001.app.service.IAppService
import com.sxd.app_stm_001.scala.function.POCElasticsearchSinkFunction
import com.sxd.app_stm_001.scala.sink.ESKafkaRealTimeSink
import com.sxd.app_stm_001.scala.utils.kie.KIEUtils
import com.sxd.app_stm_001.scala.utils.{Constants, JsonUtils}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.collector.selector.OutputSelector
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, _}
import org.apache.flink.streaming.connectors.elasticsearch5.ElasticsearchSink
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.kie.api.runtime.KieSession

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}


/**
  * Created by Administrator on 2018/8/4.
  */
class AppWork(appService:IAppService) extends Serializable{

  var app:App=_
  def run(appid:String) : Unit = {
    app= appService.getApp(new App(appid))
    val source = app.getSource
    val channel = app.getChannel
    val app_id=appid
    val app_name=app.getAppName
    val kafkaSrcProp=new Properties()
    kafkaSrcProp.put(Constants.BOOTSTRAP_SERVERS_KEY,source.getKafkaHosts)
    kafkaSrcProp.put(Constants.MAX_REQUEST_SIZE_KEY,Constants.MAX_REQUEST_SIZE_VALUE)
    kafkaSrcProp.put(Constants.MAX_PARTITION_FETCH_BYTES_KEY,Constants.MAX_PARTITION_FETCH_BYTES_VALUE)
    kafkaSrcProp.put(Constants.ZOOKEEPER_CONNECT_KEY,source.getZokkeeperHost)
    kafkaSrcProp.put(Constants.GROUP_ID_KEY,source.getGroups)
    val kafkaConsumer = new FlinkKafkaConsumer010(source.getTopic,new SimpleStringSchema,kafkaSrcProp)
    val env=StreamExecutionEnvironment.getExecutionEnvironment
    //这里设置为Flink的处理时间
    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)
    env.setParallelism(6)
    env.getConfig.disableSysoutLogging
    env.enableCheckpointing(2000)

    val kafkaMessage=env.addSource(kafkaConsumer).filter(x=>{
      try{
        JsonUtils.strToJsonArray(x).size() > 0
      }catch{
        case ex:Exception=>
          false
      }
    }).flatMap(bthData=>{
      val retArrayBuff=ArrayBuffer[String]()
      val jsonObject=new JsonObject
      val bthId=UUID.randomUUID().toString.replaceAll("-","")
      jsonObject.addProperty("bthId",bthId)
      jsonObject.addProperty("timestamp",System.currentTimeMillis())
      jsonObject.addProperty("appId",app_id)
      jsonObject.addProperty("appName",app_name)
      val jsonArray=JsonUtils.strToJsonArray(bthData)
      val tmpJsonArray=new JsonArray
      for(i <- 0 until jsonArray.size()){
        val jsonObj=JsonUtils.strToJson(jsonArray.get(i).toString)
        jsonObj.addProperty("bthId",bthId)
        jsonObj.addProperty("id",UUID.randomUUID().toString.replaceAll("-",""))
        tmpJsonArray.add(jsonObj)
      }
      retArrayBuff += JsonUtils.toJson(tmpJsonArray)
      retArrayBuff += JsonUtils.toJson(jsonObject)
      retArrayBuff
    }).split(new OutputSelector[String] {
      override def select(value: String): lang.Iterable[String] = {
        val lst=new util.ArrayList[String]()
        if(StringUtils.contains(value,"appName")){
          lst.add("head")
        }else{
          lst.add("orginData")
        }
        lst
      }
    })
    val esConfig = Map(Constants.ES_CLUSTER_NAME_KEY->Constants.ES_CLUSTER_NAME_VALUE,
      "bulk.flush.max.actions"->"100")
    //val transportAddresses =List[InetSocketAddress](new InetSocketAddress("127.0.0.1", 9300))
    val transportAddresses:util.List[InetSocketAddress]=new util.ArrayList[InetSocketAddress]()
    val esAddr:Array[String]=Constants.ES_ADDR.split(",")
    for(addr<-esAddr){
      transportAddresses.add(new InetSocketAddress(addr.split(":")(0), addr.split(":")(1).toInt))
    }
    val headESConfig:Map[String,String]=Map(Constants.ES_INDEX_KEY->"idx_app_data_batch_",
      Constants.ES_TYPE_KEY->"app_data_batch_")

    val headStream=kafkaMessage.select("head")

    headStream.addSink(new ElasticsearchSink[String](esConfig,transportAddresses,new POCElasticsearchSinkFunction(headESConfig)))
    //原始数据写入ES
    val orginDS= kafkaMessage.select("orginData")
    val orginESConfig:Map[String,String]=Map(Constants.ES_INDEX_KEY->"idx_app_stm_001_ods",
      Constants.ES_TYPE_KEY->"app_stm_001_ods")
    orginDS.flatMap(x=>{
      JsonUtils.strToJsonArray(x)
    }).map(x=>JsonUtils.toJson(x)).addSink(new ElasticsearchSink[String](esConfig,transportAddresses,new POCElasticsearchSinkFunction(orginESConfig)))


    //数据处理#############################################################33
    val ds: DataStream[String] =orginDS.flatMap(line=> {
      val jsonArr:JsonArray= JsonUtils.strToJsonArray(line)
      val dataJsonArr:JsonArray=new JsonArray
      val errorJsonArr=new JsonArray

      val retArrayBuff=ArrayBuffer[String]()

      if(jsonArr!=null && jsonArr.size()>0) {
        for (i <- 0 until jsonArr.size()) {
          var errorJsonObj=new JsonObject
          val jsonStr=jsonArr.get(i).toString
          try {
            var flag = true
            val dataJsonObj=JsonUtils.strToJson(jsonStr)
            breakable(
              for (i <- 0 until source.getFields.size()) {
                if (!StringUtils.contains(jsonStr, source.getFields.get(i).getName)) {
                  flag = false
                  errorJsonObj=dataJsonObj
                  errorJsonObj.addProperty("errorInfo",s"缺少[${source.getFields.get(i).getName}] 字段")
                  break()
                }
              }
            )

            val jsonObj=JsonUtils.strToJson(JsonUtils.toJson(dataJsonObj))
            jsonObj.addProperty("errorInfo","")
            errorJsonArr.add(jsonObj)
            //填充数据
            if (flag) {
              dataJsonArr.add(dataJsonObj)
            }else{
              errorJsonArr.add(errorJsonObj)
            }
          }catch {
            case ex:Exception=>{
            }
          }
        }
        retArrayBuff += JsonUtils.toJson(dataJsonArr)
        retArrayBuff += JsonUtils.toJson(errorJsonArr)
      }else{
        retArrayBuff
      }
    }).filter(data=>{StringUtils.isNotEmpty(data)})


    //对流进行切分
    val splitStream: SplitStream[String] =ds.split(new OutputSelector[String] {
      override def select(value: String): lang.Iterable[String] = {

        val lst:util.ArrayList[String]=new util.ArrayList[String]()
        if(value.contains("errorInfo")){
          lst.add("errorData")
        }else{
          lst.add("finalData")
        }
        lst
      }
    })
    //输出流
    val finalStream=splitStream.select("finalData").map(line=>{
      val jsonArr=JsonUtils.strToJsonArray(line)
      val retArr=new ArrayBuffer[String]()
      for(i<- 0 until jsonArr.size()){
        retArr += jsonArr.get(i).toString
      }
      retArr
    }).flatMap(data=>data).map(data=>{
        //这里的规则应该从数据库获取，可修改为动态获取
        val jsonObj:JsonObject=JsonUtils.strToJson(data)
        val kieSession:KieSession=KIEUtils.getKieContainer.newKieSession()
        kieSession.insert(jsonObj)
        kieSession.fireAllRules()
        kieSession.dispose()
        JsonUtils.toJson(jsonObj)
    })


    //中间流  异常数据
    val errorStream=splitStream.select("errorData").map(line=>{
      val jsonArr=JsonUtils.strToJsonArray(line)
      val retArr=new ArrayBuffer[String]()
      for(i<- 0 until jsonArr.size()){
        retArr += jsonArr.get(i).toString
      }
      retArr
    }).flatMap(x=>x)
    //错误结果写入ES

    val errorMapConfig:Map[String,String]=Map(Constants.ES_INDEX_KEY->"idx_app_stm_001_mid",
      Constants.ES_TYPE_KEY->"app_stm_001_mid")
    errorStream.addSink(new ElasticsearchSink[String](esConfig,transportAddresses,new POCElasticsearchSinkFunction(errorMapConfig)))


    val kafkaParams = Map(
      Constants.BOOTSTRAP_SERVERS_KEY -> channel.getKafkaHosts,
      "key.serializer" -> Constants.serializer_value,
      "value.serializer"-> Constants.serializer_value
    )
    val mapConfig:Map[String,String]=Map(Constants.ES_INDEX_KEY->Constants.ES_INDEX_NAME_CHL_TMP,
      Constants.ES_TYPE_KEY->"app_stm_001_cnl")
    //数据写入ES中
    finalStream.addSink(new ESKafkaRealTimeSink(esConfig,transportAddresses,new POCElasticsearchSinkFunction(mapConfig), channel.getTopic,kafkaParams))
    env.execute(app.getAppName)
  }
}
