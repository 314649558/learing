package com.sxd.app_stm_002.work.cep.function

import java.util

import com.sxd.app_stm_002.work.utils.JsonUtils
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
