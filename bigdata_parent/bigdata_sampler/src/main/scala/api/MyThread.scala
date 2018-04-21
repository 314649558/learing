package api

import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue, LinkedBlockingQueue}

import scala.util.control.Breaks


class MyThread(val blockingQueue: LinkedBlockingQueue[String]) extends Runnable{


  override def run(): Unit = {


    while(true){
      if(blockingQueue.size()>0){
        val str=blockingQueue.poll()
        println(s"""${str} ${System.currentTimeMillis()}""")
      }
    }
  }


}


object MyThread{
  def main(args: Array[String]): Unit = {
    val blockingQueue = new LinkedBlockingQueue[String]()

    //new Thread(new MyThread(blockingQueue)).start()
    val bTime=System.currentTimeMillis()
    var breakFlag=false
    import scala.util.control.Breaks._
    breakable {
      for (i <- 1 to Integer.MAX_VALUE) {
        val str =s"""source[$i]"""
        blockingQueue.put(str)
        //println(i)

        if (System.currentTimeMillis() - bTime >=2000) {
          break()
        }
        /* if(i%10==0){
        Thread.sleep(2000)
      }*/
      }
    }

    println(blockingQueue.size())



  }


}
