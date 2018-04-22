package com.bigdata.spark.streaming

import java.util.concurrent.BlockingQueue

class MyRunnable(blockingQueue:BlockingQueue[String]) extends Runnable{
  override def run(): Unit = {

    while (true) {
      if (blockingQueue.size() > 0) {
        println("---" + blockingQueue.poll())
      }
      println("--------------------------------------------")
      Thread.sleep(2000)

    }
  }
}
