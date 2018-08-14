package com.sxd.citic.drools.core.utils

import java.util.Properties

/**
  * Created by Administrator on 2018/8/4.
  */
object LoadConfigUtils {
  private val CONFIG_DIR="/conf/"
  def  getProperties(fileName:String):Properties={
    val inputStream = getClass.getResourceAsStream(s"${CONFIG_DIR}${fileName}")
    try {
      val properties = new Properties()
      properties.load(inputStream)
      properties
    } finally {
      inputStream.close()
    }
  }
  def getPropertiesValue(propertiesName:String)(implicit properties: Properties):Option[String]={

    if(properties!=null){
      try {
        Some(properties.getProperty(propertiesName).toString)
      }catch {
        case ex:Exception=>{
          ex.printStackTrace()
          None
        }
      }
    }else{
      None
    }
  }


  def main(args: Array[String]): Unit = {
    implicit val properties=LoadConfigUtils.getProperties("poc.properties")
    println(LoadConfigUtils.getPropertiesValue("mysql.url").get)
    println(LoadConfigUtils.getPropertiesValue("mysql.driver").get)
    println(LoadConfigUtils.getPropertiesValue("mysql.password").get)
    println(LoadConfigUtils.getPropertiesValue("mysql.username").get)
}


}
