package test

import java.util.concurrent.CountDownLatch

/**
  * Created by Administrator on 2018/12/9.
  */
abstract class AbstractClassDemo {


  def wakeup():Unit

  def startupComplete():Unit={
    println("-------------startupComplete--------------")
    wakeup()
  }

}


class AbstractSubClassDemo extends AbstractClassDemo{
  @Override
  def wakeup(): Unit = {
    println("call wakeup method")
  }
}

object AbstractSubClassDemoObj{
  def main(args: Array[String]): Unit = {
    new AbstractSubClassDemo().startupComplete()
  }
}



