package com.sxd.citic.cep.main

import java.util.{Properties, UUID}

import com.google.gson.{JsonArray, JsonObject}
import com.sxd.citic.cep.core.bean.{AppBean, FieldMappingBean, RuleExpressionBean}
import com.sxd.citic.cep.core.common.QueryMysqlUtils
import com.sxd.citic.cep.core.utils.{Constants, JsonUtils}
import com.sxd.citic.cep.utils.ExpressionUtils
import com.sxd.citic.cep.wartermarker.BoundedLatenessWatermarkAssigner
import org.apache.commons.lang3.StringUtils
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.cep.scala.CEP
import org.apache.flink.cep.scala.pattern.Pattern
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer010, FlinkKafkaProducer010}

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

/**
  * Created by Administrator on 2018/8/1.
  */
object PocCepExample2 {
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
    val rules=List(RuleExpressionBean("toSalesPct","costAmt/netSalesAmt"),
      RuleExpressionBean("imPct","(retailAmt - costAmt) / retailAmt")
      )

    val kafkaSrcProp=new Properties()
    kafkaSrcProp.put(Constants.BOOTSTRAP_SERVERS_KEY,appBean.srcKafkaHosts)
    kafkaSrcProp.put(Constants.MAX_REQUEST_SIZE_KEY,Constants.MAX_REQUEST_SIZE_VALUE)
    kafkaSrcProp.put(Constants.MAX_PARTITION_FETCH_BYTES_KEY,Constants.MAX_PARTITION_FETCH_BYTES_VALUE)
    kafkaSrcProp.put(Constants.ZOOKEEPER_CONNECT_KEY,appBean.srcZookeeperHosts)
    kafkaSrcProp.put(Constants.GROUP_ID_KEY,appBean.srcGroups)

    val kafkaConsumer = new FlinkKafkaConsumer010(appBean.srcTopic,new SimpleStringSchema,kafkaSrcProp)
    kafkaConsumer.assignTimestampsAndWatermarks(new BoundedLatenessWatermarkAssigner(0))
    val env=StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(6)
    env.getConfig.disableSysoutLogging
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)


    val kafkaMessage=env.addSource(kafkaConsumer)
    val p=Pattern.begin[String]("item_first")
              .where(data=>{
                //必须包含的字段
                def checkMustField :Boolean= {
                  var flag = true
                  breakable(
                    for (field <- fieldSrcArr) {
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

    val ds=CEP.pattern(kafkaMessage,p).select(pattern=>{
      val jsonStr=pattern.get("item_first").get.toList(0)


      val jsonArr: JsonArray =JsonUtils.strToJsonArray(jsonStr)

      val head:JsonObject=new JsonObject

      head.addProperty("appId",appBean.appId)
      head.addProperty("bthId",UUID.randomUUID().toString)
      head.addProperty("consume",0)
      head.addProperty("status",0)
      head.addProperty("timeStamp",System.currentTimeMillis())
      head.addProperty("produce",jsonArr.size())

      val retJsonObj=new JsonObject

      retJsonObj.add("head",head)


      val jsonData: JsonArray =new JsonArray

      for(k <- 0 until jsonArr.size()){

        val jsonObj=JsonUtils.strToJson(jsonArr.get(k).toString)
        val tmpJsonObj:JsonObject=new JsonObject
        for(fieldMapping <- fieldTarArr){
          val fieldName=if (!StringUtils.equals(fieldMapping.filedMappingName,"") ) fieldMapping.filedMappingName else fieldMapping.fieldName
          try{
            tmpJsonObj.addProperty(fieldMapping.fieldName, jsonObj.get(fieldName).getAsString)
          }catch {
            case ex:Exception=>{
              tmpJsonObj.addProperty(fieldMapping.fieldName, "")
              if(rules!=null && rules.size>0){
                for(rule<-rules){
                  if(StringUtils.isEmpty(rule.ruleType) && StringUtils.equals(fieldName,rule.filedName)){
                    tmpJsonObj.addProperty(rule.filedName,ExpressionUtils.expressionParse(rule.expression,jsonObj))
                  }
                }
              }
            }
          }
        }
        jsonData.add(tmpJsonObj)
      }
      retJsonObj.add("data",jsonData)
      JsonUtils.toJson(retJsonObj)
    })
    val kafkaChlProp=new Properties()
    kafkaChlProp.put(Constants.BOOTSTRAP_SERVERS_KEY,appBean.chlKafkatHosts)
    kafkaChlProp.put(Constants.ZOOKEEPER_CONNECT_KEY,appBean.chlZookeeperHosts)
    kafkaChlProp.put(Constants.MAX_REQUEST_SIZE_KEY,Constants.MAX_REQUEST_SIZE_VALUE)
    val kafkaProducer = new FlinkKafkaProducer010(appBean.chlTopic,new SimpleStringSchema,kafkaChlProp)
    ds.print()
    ds.addSink(kafkaProducer)
    env.execute("PocCepExample2")
  }
}
