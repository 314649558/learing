package com.sxd.app_stm_001.scala.utils.kie

import org.kie.api.builder.{Message, Results}
import org.kie.api.io.ResourceType
import org.kie.internal.utils.KieHelper

/**
  * Created by Administrator on 2018/8/4.
  */
object ReloadDroolsRule {

  /**
    * 重新加载规则
    * @param rule
    */
  def reload(rule:String):Unit={
    val kieServices=KIEBuilder.getKieServices
    val kfs = kieServices.newKieFileSystem
    kfs.write("src/main/resources/rules/temp.drl", rule)

    val kieBuilder = kieServices.newKieBuilder(kfs).buildAll

    val results = kieBuilder.getResults
    if (results.hasMessages(Message.Level.ERROR)) {
      println(results.getMessages)
      throw new IllegalStateException("### errors ###")
    }

    /**
      * 如果规则变化需要重新设置Container,设置Container比较消耗性能  不要轻易重新创建Contanier
      */
    KIEUtils.reloadKieContainer(kieServices.newKieContainer(kieServices.getRepository.getDefaultReleaseId))

    println("reload sucessful")
  }


  /**
    * 通过KieHelper加载规则
    * @param rule
    */
  def reloadByHelper(rule:String):Unit={
    val kieHelper:KieHelper=new KieHelper()
    kieHelper.addContent(rule, ResourceType.DRL)
    val results :Results= kieHelper.verify
    if (results.hasMessages(Message.Level.ERROR)) {
      println(results.getMessages)
      throw new IllegalStateException("### errors ###")
    }
    val kieContainer = kieHelper.getKieContainer
    KIEUtils.reloadKieContainer(kieContainer)
    println("reloadByHelper sucessful")

  }


}
