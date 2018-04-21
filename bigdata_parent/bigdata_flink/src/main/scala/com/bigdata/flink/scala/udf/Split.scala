package com.bigdata.flink.scala.udf

import org.apache.flink.table.functions.TableFunction

class Split(spliter:String) extends TableFunction[(String, Int)]{
  def eval(str:String):Unit={
    str.split(spliter).foreach(x=>{
      collect((x,x.length))
    })
  }
}