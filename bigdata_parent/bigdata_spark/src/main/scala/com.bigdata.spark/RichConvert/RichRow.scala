package com.bigdata.spark.RichConvert
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.Row
class RichRow(self:Row) {
  def getRowValue(key:String):String={
    try{
      val value=self.getAs[String](key)
      if(StringUtils.isEmpty(value)){
        ""
      }else{
        value
      }
    }catch{
      case e:Exception => {
        ""
      }
    }
  }

}
