package com.sxd.citic.drools.main

import java.net.InetSocketAddress
import java.util.{Properties, UUID}
import java.{lang, util}

import com.google.gson.{JsonArray, JsonObject}
import com.sxd.citic.drools.core.common.QueryMysqlUtils
import com.sxd.citic.drools.core.utils.{Constants, JsonUtils}
import com.sxd.citic.drools.function.{DroolsRichMapFunction, POCElasticsearchSinkFunction}
import com.sxd.citic.drools.sink.ESKafkaRealTimeSink
import com.sxd.citic.drools.utils.KIEUtils
import org.apache.commons.lang3.StringUtils
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.collector.selector.OutputSelector
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.elasticsearch5.ElasticsearchSink
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.kie.api.runtime.KieSession

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}


/**
  * Created by Administrator on 2018/8/4.
  */
object AppWork extends Serializable{

  def main(args: Array[String]): Unit = {

    val paramTool: ParameterTool =ParameterTool.fromArgs(args)

    val app_id=paramTool.get("appid","2")
    val app= QueryMysqlUtils.getAPP(app_id)
    val source = QueryMysqlUtils.getSrcField(app.dsId)
    val channel = QueryMysqlUtils.getChlField(app.cnId)
    val kafkaSrcProp=new Properties()
    kafkaSrcProp.put(Constants.BOOTSTRAP_SERVERS_KEY,app.srcKafkaHosts)
    kafkaSrcProp.put(Constants.MAX_REQUEST_SIZE_KEY,Constants.MAX_REQUEST_SIZE_VALUE)
    kafkaSrcProp.put(Constants.MAX_PARTITION_FETCH_BYTES_KEY,Constants.MAX_PARTITION_FETCH_BYTES_VALUE)
    kafkaSrcProp.put(Constants.ZOOKEEPER_CONNECT_KEY,app.srcZookeeperHosts)
    kafkaSrcProp.put(Constants.GROUP_ID_KEY,app.srcGroups)
    val kafkaConsumer = new FlinkKafkaConsumer010(app.srcTopic,new SimpleStringSchema(),kafkaSrcProp)
    val env=StreamExecutionEnvironment.getExecutionEnvironment
    //这里设置为Flink的处理时间
    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)
    env.setParallelism(4)
    env.getConfig.disableSysoutLogging
    env.enableCheckpointing(2000)

    val kafkaMessage1=env.addSource(kafkaConsumer)

    val kafkaMessage=kafkaMessage1.filter(x=>{
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
      jsonObject.addProperty("appName",app.appName)
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
              for(fieldMappingBean <- source){
                if (!StringUtils.contains(jsonStr,fieldMappingBean.fieldName)) {
                  flag = false
                  errorJsonObj=dataJsonObj
                  errorJsonObj.addProperty("errorInfo",s"缺少[${fieldMappingBean.fieldName}] 字段")
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
    }).flatMap(data=>data).map(new DroolsRichMapFunction(app_id))
    //中间流  异常数据
    val errorStream=splitStream.select("errorData").map(line=>{
      val jsonArr=JsonUtils.strToJsonArray(line)
      val retArr=new ArrayBuffer[String]()
      for(i<- 0 until jsonArr.size()){
        retArr += jsonArr.get(i).toString
      }
      retArr
    }).flatMap(x=>x).map(new DroolsRichMapFunction(app_id))
    //错误结果写入ES
    val errorMapConfig:Map[String,String]=Map(Constants.ES_INDEX_KEY->"idx_app_stm_001_mid",
      Constants.ES_TYPE_KEY->"app_stm_001_mid")
    errorStream.addSink(new ElasticsearchSink[String](esConfig,transportAddresses,new POCElasticsearchSinkFunction(errorMapConfig)))
    val kafkaParams = Map(
      Constants.BOOTSTRAP_SERVERS_KEY -> app.chlKafkatHosts,
      "key.serializer" -> Constants.serializer_value,
      "value.serializer"-> Constants.serializer_value
    )
    val mapConfig:Map[String,String]=Map(Constants.ES_INDEX_KEY->Constants.ES_INDEX_NAME_CHL_TMP,
      Constants.ES_TYPE_KEY->"app_stm_001_cnl")
    //数据写入ES中
    finalStream.addSink(new ESKafkaRealTimeSink(esConfig,transportAddresses,new POCElasticsearchSinkFunction(mapConfig),channel))

    env.execute(app.appName)
  }
}
