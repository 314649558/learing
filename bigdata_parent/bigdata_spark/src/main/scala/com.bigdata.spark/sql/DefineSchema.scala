package com.bigdata.spark.sql

import org.apache.spark.sql.types.{StringType, StructField, StructType}

object DefineSchema {
  val schema=StructType(Array(
    StructField("id",StringType,nullable = true),
    StructField("kehuzhao",StringType,nullable = true),
    StructField("zhanghao",StringType,nullable = true)
  ))
}
