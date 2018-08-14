package com.sxd.citic.drools.function

import java.sql.{Connection, ResultSet}

import com.google.gson.JsonObject
import com.sxd.citic.drools.core.common.{MysqlDBManager, SQLConstants}
import com.sxd.citic.drools.core.utils.JsonUtils
import com.sxd.citic.drools.utils.{KIEUtils, ReloadDroolsRule}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration
import org.kie.api.runtime.{KieContainer, KieSession}
/**
  * Created by Administrator on 2018/8/14.
  * 规则引擎处理方法
  */
class DroolsRichMapFunction(appid:String) extends RichMapFunction[String,String]{
  var conn:Option[Connection]=_
  var kieContainer:KieContainer=_
  override def open(parameters: Configuration): Unit ={
    reloadDrools
    this.kieContainer=KIEUtils.getKieContainer
  }

  override def close(): Unit = {
    MysqlDBManager.closeConnection(conn)
  }
  override def map(data: String): String = {
    try {
      val jsonObj: JsonObject = JsonUtils.strToJson(data)
      val kieSession: KieSession = this.kieContainer.newKieSession()
      kieSession.insert(jsonObj)
      kieSession.fireAllRules()
      kieSession.dispose()
      JsonUtils.toJson(jsonObj)
    }catch {
      case ex:Exception=>{
        data
      }
    }
  }


  private def reloadDrools = {
    this.conn = MysqlDBManager.getConection
    val pstmt = MysqlDBManager.getPstmt(conn, SQLConstants.loadDroolsSql, Array(appid))
    val resultSet: Option[ResultSet] = MysqlDBManager.getResultSet(pstmt)
    try {
      resultSet match {
        case None =>
        case _ => {
          if (resultSet.get.next()) {
            val isChange = resultSet.get.getString("is_change")
            if (StringUtils.equals(isChange, "1")) {
              val drlContent = resultSet.get.getString("drl_content")
              ReloadDroolsRule.reload(drlContent)
            }
          }
        }
      }
    } finally {
      MysqlDBManager.closeResultSet(resultSet)
      MysqlDBManager.closePstmt(pstmt)
    }
  }
}
