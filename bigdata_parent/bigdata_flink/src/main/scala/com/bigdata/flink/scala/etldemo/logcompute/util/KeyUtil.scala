package com.bigdata.flink.scala.etldemo.logcompute.util

import com.roundeights.hasher.Algo

object KeyUtil {

  @throws(classOf[Exception])
  def hash(array:collection.mutable.ArrayBuffer[String]): String = {
    array.foldLeft(Algo.sha1.foldable){
      (accum,str)=>accum(str)
    }.done.hex
  }
}
