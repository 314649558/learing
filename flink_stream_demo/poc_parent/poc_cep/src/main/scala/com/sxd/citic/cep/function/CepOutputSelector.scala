package com.sxd.citic.cep.function

import java.{lang, util}

import org.apache.flink.streaming.api.collector.selector.OutputSelector

/**
  * Created by yuanhailong on 2018/8/9.
  *
  * 自定义分流函数
  *
  */
class CepOutputSelector extends OutputSelector[String]{
  override def select(value: String): lang.Iterable[String] = {
    val lst:util.ArrayList[String]=new util.ArrayList[String]()
    if(value.contains("errorInfo")){
      lst.add("errorData")
    }else{
      lst.add("finalData")
    }
    lst
  }
}
