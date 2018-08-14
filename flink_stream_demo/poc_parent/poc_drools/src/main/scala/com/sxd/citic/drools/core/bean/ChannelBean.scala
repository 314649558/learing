package com.sxd.citic.drools.core.bean

import scala.collection.mutable.ArrayBuffer

/**
  * Created by Administrator on 2018/8/1.
  */
case class SourceBean(clusterAddr:String,zkAddr:String,topic:String,fieldMap:ArrayBuffer[FieldMappingBean])
case class ChannelBean(clusterAddr:String,  zkAddr:String,topic:String, fieldMap:ArrayBuffer[FieldMappingBean])
