package com.bigdata.flink.scala.etldemo.logcompute

import java.util.Properties

import com.bigdata.flink.scala.etldemo.logcompute.Constants._
import com.bigdata.flink.scala.etldemo.logcompute.bean.{ComputeResult, LogEvent}
import com.bigdata.flink.scala.etldemo.logcompute.function.{AggregateFunc, ApplyComputeRule}
import com.bigdata.flink.scala.etldemo.logcompute.schema.{ComputeResultSerializeSchema, LogEventDeserializationSchema}
import com.bigdata.flink.scala.etldemo.logcompute.source.ConfSource
import com.bigdata.flink.scala.etldemo.logcompute.wartermarker.BoundedLatenessWatermarkAssigner
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.assigners.{SlidingEventTimeWindows, TumblingEventTimeWindows}
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer010, FlinkKafkaProducer010}
import org.apache.flink.streaming.api.windowing.time.Time
/**
  * main function input args
  *
  * master.com log_group1 input_topic,log_group2,out_topic,localhost:8081 30
  */
object Launcher {

  def main(args: Array[String]): Unit = {

    val env:StreamExecutionEnvironment=StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    /**enable checkpoint   60s 执行一次checkpoint*/
    env.enableCheckpointing(60000L)
    val checkpointConf=env.getCheckpointConfig
    checkpointConf.setMinPauseBetweenCheckpoints(30000L)
    checkpointConf.setCheckpointTimeout(8000L)
    checkpointConf.enableExternalizedCheckpoints(ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)


    /** Kafka Consumer */
    val consumerProps=new Properties()
    consumerProps.setProperty(KEY_BOOTSTRAP_SERVERS,args(0))
    consumerProps.setProperty(KEY_GROUP_ID,args(1))
    val consumer=new FlinkKafkaConsumer010[LogEvent](
      args(2),
      new LogEventDeserializationSchema,
      consumerProps)

    val producerProps=new Properties()
    producerProps.setProperty(KEY_BOOTSTRAP_SERVERS,args(0))
    producerProps.setProperty(KEY_GROUP_ID, args(3))

    val producer =new FlinkKafkaProducer010[ComputeResult](
      args(4),
      new ComputeResultSerializeSchema(args(4)),
      producerProps
    )

    /* at_ least_once 设置 */
    producer.setLogFailuresOnly(false)
    producer.setFlushOnCheckpoint(true)

    val confStream = env.addSource(new ConfSource(args(5)))
                        .setParallelism(1)
                        .broadcast

    env.addSource(consumer)
          .connect(confStream)
          .flatMap(new ApplyComputeRule)
          .assignTimestampsAndWatermarks(new BoundedLatenessWatermarkAssigner(args(6).toInt))
          .keyBy(Constants.FIELD_KEY)
          .window(TumblingEventTimeWindows.of(Time.minutes(1)))
          .reduce(_ + _)
          .keyBy(Constants.FIELD_KEY,Constants.FIELD_PERIODS)
          .window(SlidingEventTimeWindows.of(Time.minutes(60),Time.minutes(1)))
          .apply(new AggregateFunc)
          .addSink(producer)


    env.execute("log_compute")


  }

}
