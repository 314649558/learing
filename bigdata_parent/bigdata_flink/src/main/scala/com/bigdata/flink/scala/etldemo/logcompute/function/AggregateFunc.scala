package com.bigdata.flink.scala.etldemo.logcompute.function

import com.bigdata.flink.scala.etldemo.logcompute.bean.ComputeResult
import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.streaming.api.scala.function.WindowFunction
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector
import com.bigdata.flink.scala.etldemo.logcompute.Constants._

import scala.collection.mutable

/**
  * Created by luojiangyu on 3/18/18.
  */
class AggregateFunc extends WindowFunction[ComputeResult, ComputeResult, Tuple, TimeWindow]{

  override def apply(key: Tuple, window: TimeWindow, input: Iterable[ComputeResult], out: Collector[ComputeResult]): Unit = {
    val periods = key.getField[String](1)
    periods.split(SEP_SEMICOL).foreach((period) => {
      val end = window.getEnd
      val start = end - period.toLong * 1000L
      val tuples=input.filter(_.metadata(FIELD_TIMESTAMP).asInstanceOf[Long] >= start)

      if (tuples.nonEmpty) {
        val tuple = tuples.map(t => {
          val key = t.metadata(FIELD_UNIQUE_ID) + period
          ComputeResult(
            key,
            metadata = mutable.HashMap(
              FIELD_DATASOURCE -> t.metadata(FIELD_DATASOURCE),
              FIELD_TIMESTAMP -> (end - period.toLong * 1000).asInstanceOf[AnyRef],
              FIELD_PERIOD -> period
            ),
            dimensions = t.dimensions,
            values = t.values

          )
        }).reduce(_ + _)
        out.collect(tuple)
      }
    })
  }
}
