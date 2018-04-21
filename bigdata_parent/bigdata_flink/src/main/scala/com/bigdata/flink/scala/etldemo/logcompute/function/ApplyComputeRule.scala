package com.bigdata.flink.scala.etldemo.logcompute.function

import com.bigdata.flink.scala.etldemo.logcompute.Constants
import com.bigdata.flink.scala.etldemo.logcompute.bean.{ComputeConf, ComputeResult, LogEvent}
import com.bigdata.flink.scala.etldemo.logcompute.util.KeyUtil
import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction
import org.apache.flink.util.Collector

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class ApplyComputeRule extends CoFlatMapFunction[LogEvent,ComputeConf,ComputeResult]{

  private var computeConf = ComputeConf(0,Array())

  override def flatMap1(logEvent: LogEvent, collector: Collector[ComputeResult]): Unit = {
    applyConfToLog(logEvent, computeConf).foreach(collector.collect(_))
  }

  override def flatMap2(in2: ComputeConf, collector: Collector[ComputeResult]): Unit = {
    computeConf=in2
  }


  def applyConfToLog(logEvent: LogEvent,computeConf: ComputeConf):ArrayBuffer[ComputeResult]={
    var array=collection.mutable.ArrayBuffer[ComputeResult]()
    computeConf.data.foreach(rule=>{
      var valid=true
      rule.filters.foreach(f=>{
        if (logEvent.content.contains(f.name)){
          val v=logEvent.content(f.name)

          f.operation match {
            case Constants.GT => valid = v.toDouble > f.value.toDouble
            case Constants.EQ => valid = v == f.value
            case Constants.LT => valid = v.toDouble < f.value.toDouble
            case Constants.GE => valid = v.toDouble >= f.value.toDouble
            case Constants.LE => valid = v.toDouble <= f.value.toDouble
            case Constants.NE => valid = v != f.value
            case _ => valid=false
          }

        }
      })

      if(valid){
        val elmts=rule.groupBy.foldLeft(collection.mutable.ArrayBuffer[String](rule.id.toString)){
          (array,dimessions)=>{
            array += logEvent.content.getOrElse(dimessions,Constants.VALUE_UNDEFINED)
          }
        }

        val key=KeyUtil.hash(elmts)

        val dimensions = rule.groupBy.foldLeft(mutable.HashMap[String, String]()) {
          (map, dimension) => {
            map += (dimension -> logEvent.content.getOrElse(dimension, Constants.VALUE_UNDEFINED))
          }
        }


        val meta=collection.mutable.HashMap(
          Constants.FIELD_UNIQUE_ID -> key,
          Constants.FIELD_DATETIME -> logEvent.dateTime,
          Constants.FIELD_TIMESTAMP -> (logEvent.dateTime.getMillis).asInstanceOf[AnyRef],
          Constants.FIELD_DATASOURCE -> rule.id.toString,
          Constants.FIELD_TIMESTAMP_INTERNAL -> logEvent.dateTime.getMillis.asInstanceOf[AnyRef]
        )


        val successCount= if(logEvent.content(Constants.DIMENSION_STATUS).toInt < 400) Constants.VALUE_DOUBLE_1 else Constants.VALUE_DOUBLE_1

        val values = collection.mutable.HashMap(
          Constants.DIMENSION_RT -> logEvent.content.getOrElse[String](Constants.DIMENSION_RT,Constants.VALUE_STRING_0).toDouble,
          Constants.FIELD_COUNT -> 1.0,
          Constants.FIELD_SUCCESS_COUNT -> successCount
        )
        val periods = rule.periods.mkString(Constants.SEP_SEMICOL)

        array += ComputeResult(key,meta,dimensions,values,periods)

      }

    })
    array
  }

}
