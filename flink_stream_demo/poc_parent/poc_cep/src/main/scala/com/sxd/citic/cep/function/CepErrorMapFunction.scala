package com.sxd.citic.cep.function

import java.util

import com.sxd.citic.cep.core.utils.JsonUtils
import org.apache.flink.api.common.functions.MapFunction

/**
  * Created by Administrator on 2018/8/9.
  */
class CepErrorMapFunction extends MapFunction[String,util.ArrayList[String]]{
  override def map(value: String): util.ArrayList[String] = {
    val lst:util.ArrayList[String]=new util.ArrayList[String]()
    val jsonArr=JsonUtils.strToJsonArray(value);
    for(i <- 0 until jsonArr.size()){
      lst.add(jsonArr.get(i).toString)
    }
    lst
  }
}
