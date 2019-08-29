package com.bigdata.flink.scala.state

import org.apache.flink.api.common.state.{ListState, ListStateDescriptor}
import org.apache.flink.api.common.typeinfo.{TypeHint, TypeInformation}
import org.apache.flink.runtime.state.{FunctionInitializationContext, FunctionSnapshotContext}
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction
import org.apache.flink.streaming.api.functions.sink.SinkFunction

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * Created by Administrator on 2019/8/24.
  */
class BufferSink(threshold:Int) extends SinkFunction[(String,Int)] with CheckpointedFunction{
  @transient
  val bufferElements :ListBuffer[(String, Int)] = ListBuffer[(String, Int)]()
  var checkpointedState:ListState[(String,Int)]=_
  override def invoke(value: (String, Int), context: SinkFunction.Context[_]): Unit = {
    bufferElements += value  //将数据写入内存

    if (bufferElements.size==threshold){
      for(element<-bufferElements){
        //send to sink
      }
      bufferElements.clear()
    }
  }
  override def snapshotState(functionSnapshotContext: FunctionSnapshotContext): Unit = {
    checkpointedState.clear()   //当快照发生时将内存中的数据写入到checkpoint中
    for(element<-bufferElements){
      checkpointedState.add(element)
    }
  }
  override def initializeState(context: FunctionInitializationContext): Unit = {
    val descriptor: ListStateDescriptor[(String, Int)] =new ListStateDescriptor[(String,Int)](
      "buffered-elements",
      TypeInformation.of(new TypeHint[(String,Int)]() {})
    )
    checkpointedState=context.getOperatorStateStore.getListState(descriptor)
    if(context.isRestored){//从checkpoint中恢复状态并写入内存
      for(element<-checkpointedState.get()){
        bufferElements += element
      }
    }
  }
}
