package com.sxd.citic.cep.function

import java.util

import com.sxd.citic.cep.core.utils.JsonUtils
import org.apache.flink.api.common.functions.MapFunction

/**
  * Created by yuanhailong on 2018/8/9.
  */
class CepMapFunction extends MapFunction[String,util.ArrayList[String]]{

  override def map(value: String): util.ArrayList[String] = {
    val lst=new util.ArrayList[String]()
    val  jsonObj=JsonUtils.strToJson(value)
    lst.add(jsonObj.get("errorData").toString)
    lst.add(jsonObj.get("data").toString)
    lst
  }
}
