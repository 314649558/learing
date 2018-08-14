package com.sxd.citic.drools.core.common

import java.sql.ResultSet

import com.sxd.citic.drools.core.bean.{AppBean, AppCep, FieldMappingBean}

import scala.collection.mutable.ArrayBuffer

/**
  * Created by Administrator on 2018/8/6.
  */
object QueryMysqlUtils {

  def getAPP(id:String):AppBean={

    val conn=MysqlDBManager.getConection
    val pstmt=MysqlDBManager.getPstmt(conn, SQLConstants.loadAppSQL, Array(id))
    val rs: Option[ResultSet] = MysqlDBManager.getResultSet(pstmt)
    try {
      //获取应用
      rs match {
        case None => AppBean("", "", "", "", "", "", "", "", "", "", "", "")
        case _ => {
          if (rs.get.next()) {
            val app_id: String = rs.get.getString("app_id")
            val app_name: String = rs.get.getString("app_name")
            val ds_id: String = rs.get.getString("ds_id")
            val src_kafka_hosts: String = rs.get.getString("src_kafka_hosts")
            val src_zookeeper_hosts: String = rs.get.getString("src_zookeeper_hosts")
            val src_topic: String = rs.get.getString("src_topic")
            val src_groups: String = rs.get.getString("src_groups")
            val cn_id: String = rs.get.getString("cn_id")
            val chl_kafka_hosts: String = rs.get.getString("chl_kafka_hosts")
            val chl_zookeeper_hosts: String = rs.get.getString("chl_zookeeper_hosts")
            val chl_topic: String = rs.get.getString("chl_topic")
            val chl_groups: String = rs.get.getString("chl_groups")
            AppBean(app_id, app_name, ds_id, src_kafka_hosts, src_zookeeper_hosts, src_topic, src_groups, cn_id, chl_kafka_hosts, chl_zookeeper_hosts, chl_topic, chl_groups)
          } else {
            AppBean("", "", "", "", "", "", "", "", "", "", "", "")
          }
        }
      }
    }finally {
      MysqlDBManager.close(conn,pstmt,rs)
    }
  }

  def getSrcField(id:String): ArrayBuffer[FieldMappingBean]={

    val conn=MysqlDBManager.getConection
    val pstmt=MysqlDBManager.getPstmt(conn, SQLConstants.loadSourceSQL, Array(id))
    val result: Option[ResultSet] = MysqlDBManager.getResultSet(pstmt)
    try {
      val fieldSrcArr = new ArrayBuffer[FieldMappingBean]
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
      fieldSrcArr
    }finally {
      MysqlDBManager.close(conn,pstmt,result)
    }
  }

  def getChlField(id:String): ArrayBuffer[FieldMappingBean]={
    val conn=MysqlDBManager.getConection
    val pstmt=MysqlDBManager.getPstmt(conn, SQLConstants.loadChannelSQL, Array(id))
    val result: Option[ResultSet] = MysqlDBManager.getResultSet(pstmt)
    try {
      val fieldSrcArr = new ArrayBuffer[FieldMappingBean]
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
      fieldSrcArr
    }finally {
      MysqlDBManager.close(conn,pstmt,result)
    }
  }
}
