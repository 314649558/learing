package com.sxd.citic.cep.core.common

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}
import java.util.Properties

import com.sxd.citic.cep.core.utils.LoadConfigUtils


/**
  * Created by Administrator on 2018/8/4.
  */
object MysqlDBManager {

  def getConection:Option[Connection]={
    implicit val properties : Properties= LoadConfigUtils.getProperties("poc.properties")
    val url:String=LoadConfigUtils.getPropertiesValue("mysql.url").get
    val driver:String=LoadConfigUtils.getPropertiesValue("mysql.driver").get
    val password:String=LoadConfigUtils.getPropertiesValue("mysql.password").get
    val username:String=LoadConfigUtils.getPropertiesValue("mysql.username").get
    try {
      Class.forName(driver)
      Some(DriverManager.getConnection(url, username, password))
    }catch{
      case ex:Exception=>{
        ex.printStackTrace()
        None
      }
    }
  }

  def getPstmt(conn:Option[Connection],sql:String,params:Array[String]=Array()): Option[PreparedStatement] ={
    conn match {
      case None => None
      case _ => {
        val pstmt=conn.get.prepareStatement(sql)
        if (params != null && params.length > 0) {
          for (i <- 1 to params.length) {
            pstmt.setString(i, params(i - 1))
          }
        }
        Some(pstmt)
      }
    }
  }

  /**
    * 由于是DEMO 所以都调用查询方法
    * @return
    */
  def getResultSet(pstmt:Option[PreparedStatement]):Option[ResultSet]={
    try {
      pstmt match {
        case None => None
        case _ => {
          val rs = pstmt.get.executeQuery()
          if (rs == null) {
            None
          } else {
            Some(rs)
          }
        }
      }
    }
  }


  def closePstmt(pstmt:Option[PreparedStatement]): Unit ={
    pstmt match {
      case None=>
      case _ =>{
        if(pstmt.get!=null){
          pstmt.get.close()
        }
      }
    }
  }

  def closeConnection(conn:Option[Connection]): Unit ={
    conn match{
      case None =>
      case _ =>{
        if(conn.get!=null){
          conn.get.close()
        }
      }
    }
  }

  def closeResultSet(rs:Option[ResultSet]): Unit ={
    rs match{
      case None =>
      case _ =>{
        if(rs.get!=null){
          rs.get.close()
        }
      }
    }
  }


  def close(conn:Option[Connection],pstmt:Option[PreparedStatement],rs:Option[ResultSet]): Unit ={
    closeResultSet(rs)
    closePstmt(pstmt)
    closeConnection(conn)
  }
}
