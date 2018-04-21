package com.bigdata.flink.scala.etldemo.logcompute.wartermarker

import com.bigdata.flink.scala.etldemo.logcompute.Constants
import com.bigdata.flink.scala.etldemo.logcompute.bean.ComputeResult
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.watermark.Watermark

class BoundedLatenessWatermarkAssigner(allowLatenss:Int) extends AssignerWithPeriodicWatermarks[ComputeResult]{
  private var maxTimestamp = -1L

  override def getCurrentWatermark: Watermark = {
    new Watermark(maxTimestamp - allowLatenss*1000L)
  }

  override def extractTimestamp(t: ComputeResult, l: Long): Long = {
    val timestamp=t.metadata(Constants.FIELD_TIMESTAMP_INTERNAL).asInstanceOf[Long]
    if(timestamp > maxTimestamp){
      maxTimestamp=timestamp
    }
    timestamp
  }
}
