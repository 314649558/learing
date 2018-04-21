package com.bigdata.spark.RichConvert

import org.apache.commons.lang3.StringUtils

class RichString(self:String) {
  def getRowByStrData(delimiter:String):org.apache.spark.sql.Row={
    val split:Array[String]=self.split(delimiter).map(x=> if(StringUtils.isEmpty(x)) "" else x)
    org.apache.spark.sql.Row(split:_*)
    /**
      * or
      *
      * org.apache.spark.sql.Row.fromSeq(split.toList)
      */

  }


  def subPrefixx(index:Int):String={
    try{
      if(StringUtils.isEmpty(self)){
        ""
      }else if(self.length<=index){
        self
      }else{
        self.substring(0,index)
      }
    }catch {
      case e:Exception=>
        ""
    }
  }


}
