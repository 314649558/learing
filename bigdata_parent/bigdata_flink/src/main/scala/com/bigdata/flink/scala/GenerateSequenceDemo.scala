package com.bigdata.flink.scala

import java.{lang, util}

import org.apache.flink.api.java.ExecutionEnvironment
import org.apache.flink.api.java.DataSet
import org.apache.flink.api.java.functions.FunctionAnnotation.ForwardedFields

object GenerateSequenceDemo {

  def main(args: Array[String]): Unit = {

    val env:ExecutionEnvironment=ExecutionEnvironment.getExecutionEnvironment

    import collection.JavaConverters._
    //val someStrs:DataSet[String]=env.fromCollection(List("blockchain cloudcomputer hadoop ai","dfs bigdata").asJava)
    //val someIntegers:DataSet[lang.Long]=env.generateSequence(0,10000)

    val inital=env.fromElements(0)







  }

}
