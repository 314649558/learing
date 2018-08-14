package com.sxd.citic.drools.utils

import java.io.File

import org.apache.commons.io.FileUtils
import org.kie.api.builder._
import org.kie.api.runtime.KieContainer
import org.kie.api.{KieBase, KieServices}
import org.kie.internal.io.ResourceFactory

import scala.collection.JavaConversions._


/**
  * Created by Administrator on 2018/8/3.
  */
object KIEBuilder {


  /**
    * 规则定义根目录
    */
  val RULES_DIR="rules/"


  /**
    * 获取KieServices
    * @return
    */
  def getKieServices: KieServices={
     KieServices.Factory.get
  }


  /**
    * 获取根目录下所有的规则文件
    */
  def getRuleFiles:Option[List[File]]={
    val url=getClass.getClassLoader.getResource(RULES_DIR)
    val file=new File(url.getPath)
    if(file.exists() && file.isDirectory){
      Option(FileUtils.listFiles(file,Array("drl"),false).toList)
    }else {
      None
    }
  }


  /**
    * obtion KieFileSystem under classpath
    * @return
    */
  def kieFileSystem: KieFileSystem ={
    val kieFileSystem:KieFileSystem=getKieServices.newKieFileSystem()
    getRuleFiles match {
      case None =>
      case _=>{
        getRuleFiles.get.foreach(file=>{
          kieFileSystem.write(ResourceFactory.newClassPathResource(s"${RULES_DIR}${file.getName}","UTF-8"))
        })
      }
    }
    kieFileSystem
  }


  /**
    * Build KieContainer
    * @return
    */
  def kieContainer:KieContainer ={

    val kieServices=getKieServices

    val kieRepository = kieServices.getRepository

    kieRepository.addKieModule(new KieModule {
      override def getReleaseId: ReleaseId = kieRepository.getDefaultReleaseId
    })

    val kieBuilder = kieServices.newKieBuilder(kieFileSystem)

    val results=kieBuilder.getResults

    //构建规则时候如果有错误
    if(results.hasMessages(Message.Level.ERROR)){
      println(results.getMessages)
      throw new IllegalStateException("Happen error when build KieContainer")
    }

    kieBuilder.buildAll

    val kieContainer = kieServices.newKieContainer(kieRepository.getDefaultReleaseId)
    //KieUtils.setKieContainer(kieContainer)
    kieContainer
  }


  /**
    * 获取KIE BASE
    * @return
    */
  def kieBase:KieBase= kieContainer.getKieBase

  /**
    *  获取KIE BASE
    * @param name
    * @return
    */
  def kieBase(name:String):KieBase= kieContainer.getKieBase(name)

  //def kiePostProcessor = new KModuleBeanFactoryPostProcessor


}
