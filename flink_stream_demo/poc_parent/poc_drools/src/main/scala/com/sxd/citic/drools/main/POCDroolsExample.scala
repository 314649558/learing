package com.sxd.citic.drools.main

import java.net.InetSocketAddress
import java.util
import java.util.Properties

import com.google.gson.{JsonArray, JsonObject}
import com.sxd.citic.drools.core.bean.{AppBean, FieldMappingBean}
import com.sxd.citic.drools.core.common.QueryMysqlUtils
import com.sxd.citic.drools.core.utils.{Constants, JsonUtils}
import com.sxd.citic.drools.function.POCElasticsearchSinkFunction
import com.sxd.citic.drools.sink.ESKafkaRealTimeSink
import com.sxd.citic.drools.utils.KIEUtils
import org.apache.commons.lang3.StringUtils
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.kie.api.runtime.KieSession

import _root_.scala.collection.mutable.ArrayBuffer
import _root_.scala.util.control.Breaks._


/**
  * Created by Administrator on 2018/8/4.
  */
object POCDroolsExample {
  def main(args: Array[String]): Unit = {

    if(args==null || args.length!=1){
      println(s"<app_id>")
      return
    }
    //获取应用
    val appBean:AppBean=QueryMysqlUtils.getAPP(args(0))
    //获取数据源字段
    val fieldSrcArr:ArrayBuffer[FieldMappingBean]=QueryMysqlUtils.getSrcField(appBean.dsId)
    val fieldTarArr:ArrayBuffer[FieldMappingBean]=QueryMysqlUtils.getChlField(appBean.cnId)
    val kafkaSrcProp=new Properties()
    kafkaSrcProp.put(Constants.BOOTSTRAP_SERVERS_KEY,appBean.srcKafkaHosts)
    kafkaSrcProp.put(Constants.MAX_REQUEST_SIZE_KEY,Constants.MAX_REQUEST_SIZE_VALUE)
    kafkaSrcProp.put(Constants.MAX_PARTITION_FETCH_BYTES_KEY,Constants.MAX_PARTITION_FETCH_BYTES_VALUE)
    kafkaSrcProp.put(Constants.ZOOKEEPER_CONNECT_KEY,appBean.srcZookeeperHosts)
    kafkaSrcProp.put(Constants.GROUP_ID_KEY,appBean.srcGroups)

    val kafkaConsumer = new FlinkKafkaConsumer010(appBean.srcTopic,new SimpleStringSchema,kafkaSrcProp)
    val env=StreamExecutionEnvironment.getExecutionEnvironment
    //这里设置为Flink的处理时间
    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)
    env.setParallelism(6)
    env.getConfig.disableSysoutLogging
    val kafkaMessage=env.addSource(kafkaConsumer)

    val ds: DataStream[String] =kafkaMessage.flatMap(line=> {
      val jsonArr:JsonArray= JsonUtils.strToJsonArray(line)
      val arrayBuffer:ArrayBuffer[String]=ArrayBuffer()
      if(jsonArr!=null && jsonArr.size()>0) {
        for (i <- 0 until jsonArr.size()) {
          val value= JsonUtils.toJson(jsonArr.get(i))
          var flag=true
          breakable(
            for(fieldMappingBean<-fieldSrcArr){
              if(!StringUtils.contains(value,fieldMappingBean.fieldName)){
                flag=false
                break()
              }

            }
          )
          if(flag) arrayBuffer += value
        }
      }
      arrayBuffer
    }).filter(data=>{StringUtils.isNotEmpty(data)})
      .map(data=>{
        //这里的规则应该从数据库获取，可修改为动态获取
        val jsonObj:JsonObject=JsonUtils.strToJson(data)
        val kieSession:KieSession=KIEUtils.getKieContainer.newKieSession()
        kieSession.insert(jsonObj)
        kieSession.fireAllRules()
        kieSession.dispose()
        JsonUtils.toJson(jsonObj)
    })


    val config = Map(Constants.ES_CLUSTER_NAME_KEY->"testcluster",
        "bulk.flush.max.actions"->"100")
    val transportAddresses:util.List[InetSocketAddress]=new util.ArrayList[InetSocketAddress]()
    //(new InetSocketAddress("127.0.0.1", 9300))


    val kafkaParams = Map(
      Constants.BOOTSTRAP_SERVERS_KEY -> appBean.chlKafkatHosts,
      "key.serializer" -> Constants.serializer_value,
      "value.serializer"-> Constants.serializer_value
    )
    //数据写入ES中
   // ds.addSink(new ESKafkaRealTimeSink(config,transportAddresses,new POCElasticsearchSinkFunction(config),appBean.chlTopic,kafkaParams))
    ds.print()





    /*ds.addSink(new ElasticsearchSink[String](config,transportAddresses,new ElasticsearchSinkFunction[String] {
      def createIndexRequest(element: String): IndexRequest = {
        Requests.indexRequest.index("testdrools").`type`("drools").source(element)
      }
      override def process(element: String, runtimeContext: RuntimeContext, requestIndexer: RequestIndexer): Unit = {
        requestIndexer.add(createIndexRequest(element))
      }
    }))*/

/*
    ds.addSink(new ESKafkaRealTimeSink(config,transportAddresses,new ElasticsearchSinkFunction[String] {
      def createIndexRequest(element: String): IndexRequest = {
        Requests.indexRequest.index("testdrools").`type`("drools").source(element)
      }
      override def process(element: String, runtimeContext: RuntimeContext, requestIndexer: RequestIndexer): Unit = {
        requestIndexer.add(createIndexRequest(element))
      }
    }))*/


    env.execute("POCDroolsExample")
  }

}
