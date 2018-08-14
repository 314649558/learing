package com.sxd.citic.cep.core.common

import java.sql.ResultSet

import com.sxd.citic.cep.core.bean.{AppBean, AppCep, FieldMappingBean}
import scala.collection.mutable.ArrayBuffer

/**
  * Created by Administrator on 2018/8/6.
  */
object QueryMysqlUtils {

  def getAPP(id:String):AppBean={

    val conn=MysqlDBManager.getConection
    val pstmt=MysqlDBManager.getPstmt(conn, SQLConstants.loadAppSQL, Array(id))
    val appResult: Option[ResultSet] = MysqlDBManager.getResultSet(pstmt)
    try {
      //获取应用
      appResult match {
        case None => AppBean("", "", "", "", "", "", "", "", "", "", "", "")
        case _ => {
          if (appResult.get.next()) {
            val app_id: String = appResult.get.getString("app_id")
            val app_name: String = appResult.get.getString("app_name")
            val ds_id: String = appResult.get.getString("ds_id")
            val src_kafka_hosts: String = appResult.get.getString("src_kafka_hosts")
            val src_zookeeper_hosts: String = appResult.get.getString("src_zookeeper_hosts")
            val src_topic: String = appResult.get.getString("src_topic")
            val src_groups: String = appResult.get.getString("src_groups")
            val cn_id: String = appResult.get.getString("cn_id")
            val chl_kafka_hosts: String = appResult.get.getString("chl_kafka_hosts")
            val chl_zookeeper_hosts: String = appResult.get.getString("chl_zookeeper_hosts")
            val chl_topic: String = appResult.get.getString("chl_topic")
            val chl_groups: String = appResult.get.getString("chl_groups")
            AppBean(app_id, app_name, ds_id, src_kafka_hosts, src_zookeeper_hosts, src_topic, src_groups, cn_id, chl_kafka_hosts, chl_zookeeper_hosts, chl_topic, chl_groups)
          } else {
            AppBean("", "", "", "", "", "", "", "", "", "", "", "")
          }
        }
      }
    }finally {
      MysqlDBManager.close(conn,pstmt,appResult)
    }
  }

  def getSrcField(id:String): ArrayBuffer[FieldMappingBean]={

    val conn=MysqlDBManager.getConection
    val pstmt=MysqlDBManager.getPstmt(conn, SQLConstants.loadSourceSQL, Array(id))
    val result: Option[ResultSet] = MysqlDBManager.getResultSet(pstmt)
    val fieldSrcArr=new ArrayBuffer[FieldMappingBean]
    try {
      result match {
        case None =>
        case _ => {
          while (result.get.next()) {
            val data_type: String = result.get.getString("data_type")
            val name: String = result.get.getString("name")
            fieldSrcArr += FieldMappingBean(name, data_type)
          }
        }
      }
    }finally {
      MysqlDBManager.close(conn,pstmt,result)
    }
    fieldSrcArr
  }

  def getChlField(id:String): ArrayBuffer[FieldMappingBean]={
    val conn=MysqlDBManager.getConection
    val pstmt=MysqlDBManager.getPstmt(conn, SQLConstants.loadSourceSQL, Array(id))
    val result: Option[ResultSet] = MysqlDBManager.getResultSet(pstmt)

    val fieldSrcArr=new ArrayBuffer[FieldMappingBean]
    try{
      result match  {
        case None=>
        case _ =>{
          while(result.get.next()){
            val data_type:String=result.get.getString("data_type")
            val name:String=result.get.getString("name")
            fieldSrcArr += FieldMappingBean(name,data_type)
          }
        }
      }
    }finally {
      MysqlDBManager.close(conn,pstmt,result)
    }
    fieldSrcArr
  }

  def getAppCep(appid:String):ArrayBuffer[AppCep]={

    val conn=MysqlDBManager.getConection
    val pstmt=MysqlDBManager.getPstmt(conn, SQLConstants.loadAppCepSQL, Array(appid))
    val result: Option[ResultSet] = MysqlDBManager.getResultSet(pstmt)
    val fieldSrcArr = new ArrayBuffer[AppCep]
    try {
      result match {
        case None =>
        case _ => {
          while (result.get.next()) {
            val cepId: String = result.get.getString("cep_id")
            val appId: String = result.get.getString("cep_id")
            val fieldName: String = result.get.getString("filed_name")
            val expression: String = result.get.getString("expression")
            fieldSrcArr += AppCep(cepId, appId, fieldName, expression)
          }
        }
      }
    }finally {
      MysqlDBManager.close(conn,pstmt,result)
    }
    fieldSrcArr
  }

}
