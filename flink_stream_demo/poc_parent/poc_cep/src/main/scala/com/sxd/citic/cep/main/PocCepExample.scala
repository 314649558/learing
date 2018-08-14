package com.sxd.citic.cep.main

import java.util.Properties

import com.google.gson.JsonObject
import com.sxd.citic.cep.core.bean.{ChannelBean, FieldMappingBean, RuleExpressionBean, SourceBean}
import com.sxd.citic.cep.core.utils.{Constants, JsonUtils}
import com.sxd.citic.cep.utils.ExpressionUtils
import com.sxd.citic.cep.wartermarker.BoundedLatenessWatermarkAssigner
import org.apache.commons.lang3.StringUtils
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.cep.scala.CEP
import org.apache.flink.cep.scala.pattern.Pattern
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer010, FlinkKafkaProducer010}

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

/**
  * Created by Administrator on 2018/8/1.
  */
object PocCepExample {




  def main(args: Array[String]): Unit = {

    //来源于数据库，暂时写死
    val fieldSrcArr=new ArrayBuffer[FieldMappingBean]
    fieldSrcArr += FieldMappingBean("item_nbr","NUMBER")
    fieldSrcArr += FieldMappingBean("trade_date","DATE","yyyyMMdd")
    fieldSrcArr += FieldMappingBean("item_nbr","NUMBER")
    fieldSrcArr += FieldMappingBean("cost_amt","NUMBER")
    fieldSrcArr += FieldMappingBean("net_sales_amt","NUMBER")
    fieldSrcArr += FieldMappingBean("retail_amt","NUMBER")
    val sourceBean=SourceBean("192.168.112.101:9092","192.168.112.101:2181","pocsrc",fieldSrcArr)


    //规则定义，来源数据库
    val imPctLst=new ArrayBuffer[String]

    imPctLst+="retail_amt"
    imPctLst+="cost_amt"
    val toSalesPct=new ArrayBuffer[String]
    toSalesPct+="cost_amt"
    toSalesPct+="net_sales_amt"

    val rules=List(RuleExpressionBean("imPct","(retail_amt - cost_amt) / retail_amt"),
      RuleExpressionBean("toSalesPct","cost_amt/net_sales_amt"),
      RuleExpressionBean("","(retail_amt - cost_amt) / retail_amt > 0.5 ",Constants.EXPRESSION_RULE_TYPE.FILTER.toString))


    //来源于数据库，暂时写死
    val fieldTarArr=new ArrayBuffer[FieldMappingBean]
    fieldTarArr += FieldMappingBean("tradeDate","Date","yyyyMMdd","trade_date")
    fieldTarArr += FieldMappingBean("deptNbr","")
    fieldTarArr += FieldMappingBean("itemNbr","","","item_nbr")
    fieldTarArr += FieldMappingBean("imPct","NUMBER")
    fieldTarArr += FieldMappingBean("toSalesPct","NUMBER")
    val channelBean=ChannelBean("192.168.112.101:9092","192.168.112.101:2181","pocchl",fieldSrcArr)

    val kafkaSrcProp=new Properties()
    kafkaSrcProp.put("bootstrap.servers",sourceBean.clusterAddr)
    kafkaSrcProp.put("zookeeper.connect",sourceBean.zkAddr)
    kafkaSrcProp.put("group.id","poc_cep")

    val kafkaConsumer = new FlinkKafkaConsumer010(sourceBean.topic,new SimpleStringSchema,kafkaSrcProp)
    kafkaConsumer.assignTimestampsAndWatermarks(new BoundedLatenessWatermarkAssigner(0))
    val env=StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(2)
    env.getConfig.disableSysoutLogging
    env.getConfig.setRestartStrategy(RestartStrategies.fixedDelayRestart(4, 10000))
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

                //检测筛选条件是否满足
                def checkFilter:Boolean={
                  var flag=true
                  if(rules!=null && rules.length>0){
                    val filterRule=rules.filter(x=>StringUtils.equals(x.ruleType,Constants.EXPRESSION_RULE_TYPE.FILTER.toString))

                    val jsonObj=JsonUtils.strToJson(data)

                    if(filterRule!=null && filterRule.size>0){
                      breakable(
                        for(rule<- filterRule){
                          if(!ExpressionUtils.expressionParse(rule.expression,jsonObj).toBoolean){
                            //记录日志 并将数据暂时发送到KAFKA 便于数据审查
                            // TODO send kafka and write log
                            flag=false
                            break()
                          }
                        }
                      )
                    }
                  }
                  flag
                }
               checkMustField && checkFilter
              })

    val ds=CEP.pattern(kafkaMessage,p).select(pattern=>{
      val jsonStr=pattern.get("item_first").get.toList(0)
      val jsonObj=JsonUtils.strToJson(jsonStr)
      val retJsonObj=new JsonObject
      for(fieldMapping <- fieldTarArr){
        val fieldName=if (!StringUtils.equals(fieldMapping.filedMappingName,"") ) fieldMapping.filedMappingName else fieldMapping.fieldName
        try{
          retJsonObj.addProperty(fieldMapping.fieldName, jsonObj.get(fieldName).getAsString)
        }catch {
          case ex:Exception=>{
            retJsonObj.addProperty(fieldMapping.fieldName, "")
            if(rules!=null && rules.size>0){
              for(rule<-rules){
                if(StringUtils.isEmpty(rule.ruleType) && StringUtils.equals(fieldName,rule.filedName)){
                  retJsonObj.addProperty(rule.filedName,ExpressionUtils.expressionParse(rule.expression,jsonObj))
                }
              }
            }
          }
        }
      }
      JsonUtils.toJson(retJsonObj)
    })
    val kafkaChlProp=new Properties()
    kafkaChlProp.put("bootstrap.servers",channelBean.clusterAddr)
    kafkaChlProp.put("zookeeper.connect",channelBean.zkAddr)
    val kafkaProducer = new FlinkKafkaProducer010(channelBean.topic,new SimpleStringSchema,kafkaChlProp)
    ds.print()
    ds.addSink(kafkaProducer)
    env.execute("PocCepExample")
  }


}
