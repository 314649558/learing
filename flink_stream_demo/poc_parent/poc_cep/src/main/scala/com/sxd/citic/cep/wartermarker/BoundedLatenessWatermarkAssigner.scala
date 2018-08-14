package com.sxd.citic.cep.wartermarker

import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.watermark.Watermark


/**
  * 定义一个水位 所有的日志中必须包含timestamp字段
  * @param allowLatenss    是否允许延迟,这个一般设置为0  如果设置为大于0的数字，将会导致缓冲区延迟清理，从而导致数据重复 at-least-once 语义
  */
class BoundedLatenessWatermarkAssigner(allowLatenss:Int) extends AssignerWithPeriodicWatermarks[String]{
  private var maxTimestamp = -1L
  /**
    * 当前水位线
    * @return
    */
  override def getCurrentWatermark: Watermark = {
    new Watermark(maxTimestamp - allowLatenss*1000L)
  }


  /**
    * 抽取日志中的时间戳
    * @param element
    * @param previousElementTimestamp
    * @return
    */
  override def extractTimestamp(element: String, previousElementTimestamp: Long): Long = {
    /*var timestamp=System.currentTimeMillis()

    val jsonObj=JsonUtils.strToJson(element)

    Option(jsonObj.get(Constants.TIMESTAMP)) match {
      case None =>
      case _ => {
        timestamp=jsonObj.get(Constants.TIMESTAMP).getAsLong
      }
    }
    if (timestamp > maxTimestamp) {
      maxTimestamp = timestamp
    }
    timestamp*/

    maxTimestamp=System.currentTimeMillis()

    maxTimestamp
  }
}
