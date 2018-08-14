package com.sxd.app_stm_002.work.cep.function

import java.util

import org.apache.flink.api.common.functions.FlatMapFunction
import org.apache.flink.util.Collector


/**
  * Created by yuanhailong on 2018/8/9.
  */
class CEPFlatmapFunction extends FlatMapFunction[util.ArrayList[String],String]{
  override def flatMap(listValue: util.ArrayList[String], out: Collector[String]): Unit = {
    for(i <- 0 until  listValue.size()){
      out.collect(listValue.get(i))
    }
  }
}
