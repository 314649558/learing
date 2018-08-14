package com.sxd.app_stm_001.scala.utils.kie

import org.kie.api.runtime.KieContainer

/**
  * Created by Administrator on 2018/8/3.
  */
object KIEUtils {

  private var kieContainer:KieContainer=_

  /**
    * 获取kieContainer 如果，已经存在则直接返回，如果不存在则创建，创建KieContainer是一个消费性能的工作
    * @return
    */
  def getKieContainer:KieContainer={
    Option(kieContainer) match {
      case None=> {
        kieContainer=KIEBuilder.kieContainer
        kieContainer
      }
      case _ => kieContainer
    }
  }

  /**
    * 重新加载
    * @param kieContainer
    */
  def reloadKieContainer(kieContainer:KieContainer):Unit={

    Option(this.kieContainer) match {
      case None =>
      case _ => this.kieContainer.dispose()
    }
    this.kieContainer=kieContainer
  }

}
