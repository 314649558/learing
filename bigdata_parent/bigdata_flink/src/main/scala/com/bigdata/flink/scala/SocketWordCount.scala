package com.bigdata.flink.scala
//import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.util.serialization.SimpleStringSchema
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010

case class WordCount(key:String,count:Long)

object SocketWordCount {

  def main(args: Array[String]): Unit = {

    val inputArgs=Array("--bootstrap.servers","192.168.112.101:9092",
      "--zookeeper.connect","192.168.112.101:2181",
      "--group.id","gid001")

    val params = ParameterTool.fromArgs(inputArgs)

    //step 1 获取执行环境
    val env:StreamExecutionEnvironment=StreamExecutionEnvironment.getExecutionEnvironment

    env.getConfig.setGlobalJobParameters(params)

    //设置状态的备份方式，默认是存在内存中的
    env.setStateBackend(new FsStateBackend("file:///flink_dir/checkpoint"))


    //预防不断的checkpoint
    env.getCheckpointConfig.setMinPauseBetweenCheckpoints(100000)

    //设置强制使用Kryo序列化框架
    env.getConfig.enableForceKryo()

    //设置缓存时间，设置这个可以提高吞吐量，但是会有一定数据传输的延时，设置这个参数后，即使缓存区域没有被填满，也会在这个时间到达后立即发送数据出去
    //为了是吞吐量最大，可以设置为-1 ，只有当缓冲区满了的时候才会发送数据
    env.setBufferTimeout(100)
/*
    val param=new Configuration()
    param.setBoolean("recursive.file.enumeration",true)
*/
    //step 2 获取数据源
    val dataStream=env.socketTextStream(Constants.hostname,Constants.port,'\n')
    //step 3 转换数据
    val wc=dataStream.flatMap(_.split(" "))
        .filter(_.nonEmpty)
        .map(w=>WordCount(w,1))
        .keyBy("key")
        .timeWindow(Time.seconds(5))
        .sum("count")
    //step 4 data sink

    //wc.writeAsCsv("D://flink_dir/")
    wc.print().setParallelism(1)
    val kafkaProducer = new FlinkKafkaProducer010(Constants.OUTPUT_TOPIC,
      new SimpleStringSchema,
      params.getProperties)
    dataStream.addSink(kafkaProducer)
    //step 5 execute (lazy ，启动这个方法才会实际执行)
    env.execute()
  }

}
