package com.sxd.citic.drools.utils.elasticsearch

import java.sql.{Connection, PreparedStatement}
import java.util.Properties
import javax.sql.DataSource

import com.alibaba.druid.pool.{DruidDataSource, ElasticSearchDruidDataSourceFactory}


/**
  * Created by Administrator on 2018/8/9.
  */
object ESDruidUtils {

  private var connection:Connection=_

  def getDruidDataSource(url:String):Connection={
    if(connection==null || connection.isClosed) {
      val properties = new Properties()
      properties.put("url", url)
      val dds: DataSource = ElasticSearchDruidDataSourceFactory.createDataSource(properties)
      connection=dds.getConnection
      connection
    }else{
      connection
    }
  }


  def getPreparedStatement(sql:String)(implicit conn: Connection): PreparedStatement={
    val ps :PreparedStatement= conn.prepareStatement(sql)
    ps
  }


  def closeConnection(conn:Connection): Unit ={
    try {
      if (conn != null) {
        conn.close()
      }
    }catch {
      case ex:Exception=>ex.printStackTrace()
    }
  }

  def closePstmt(pstmt:PreparedStatement):Unit={
    try {
      if (pstmt != null) {
        pstmt.close()
      }
    }catch {
      case ex:Exception=>ex.printStackTrace()
    }
  }

}
