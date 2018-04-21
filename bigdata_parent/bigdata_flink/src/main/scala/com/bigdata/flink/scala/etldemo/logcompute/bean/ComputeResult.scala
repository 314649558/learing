package com.bigdata.flink.scala.etldemo.logcompute.bean

import com.bigdata.flink.scala.etldemo.logcompute.Constants._

case class ComputeResult(key :String,
                          metadata :scala.collection.mutable.HashMap[String,AnyRef],
                          dimensions :scala.collection.mutable.HashMap[String,String],
                          values: scala.collection.mutable.HashMap[String,Double],
                          peroids: String=VALUE_UNDEFINED) {

  def +(input:ComputeResult)={
    val rt = this.values(DIMENSION_RT)+input.values(DIMENSION_RT)
    val count = this.values(FIELD_COUNT) + input.values(FIELD_COUNT)
    val successCount= this.values(FIELD_SUCCESS_COUNT) + input.values(FIELD_SUCCESS_COUNT)

    val values=scala.collection.mutable.HashMap(
      DIMENSION_RT -> rt,
      FIELD_COUNT -> count,
      FIELD_SUCCESS_COUNT -> successCount
    )

    ComputeResult(

      key=this.key,
      metadata=this.metadata,
      dimensions=this.dimensions,
      values=values,
      peroids=this.peroids

    )

  }



}
