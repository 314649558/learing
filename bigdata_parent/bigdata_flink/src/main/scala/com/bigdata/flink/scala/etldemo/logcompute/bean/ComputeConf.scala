package com.bigdata.flink.scala.etldemo.logcompute.bean

case class ComputeConf(code:Int,data:Array[ComputeRule])

case class ComputeFilter(name :String,operation :String, value :String)

case class ComputeRule(id: Int,
                       groupBy :Array[String],
                       results :Array[String],
                       filters :Array[ComputeFilter],
                       periods :Array[Long]=Array[Long](60))

