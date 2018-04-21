package com.bigdata.flink.scala.etldemo.logcompute.schema

import com.alibaba.fastjson.JSON
import org.apache.flink.streaming.util.serialization.KeyedSerializationSchema
import com.bigdata.flink.scala.etldemo.logcompute.bean._
import org.apache.flink.api.common.typeinfo.TypeInformation

class ComputeResultSerializeSchema(topic:String) extends KeyedSerializationSchema[ComputeResult]{
  override def serializeKey(t: ComputeResult): Array[Byte] = {
    t.key.getBytes()
  }

  override def getTargetTopic(t: ComputeResult): String = topic



  override def serializeValue(t: ComputeResult): Array[Byte] = {
    JSON.toJSON(t).toString.getBytes
  }
}
