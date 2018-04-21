package com.bigdata.flink.scala.udf

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.table.api.{TableEnvironment, Types}
import org.apache.flink.table.functions.ScalarFunction

import scala.collection.mutable.ListBuffer
import scala.util.Random

class TimestampModifier extends ScalarFunction{
  def eval(t:Long) :Long={
    t%10
  }

  override def getResultType(signature: Array[Class[_]]): TypeInformation[_] ={
    Types.INT
  }

}
