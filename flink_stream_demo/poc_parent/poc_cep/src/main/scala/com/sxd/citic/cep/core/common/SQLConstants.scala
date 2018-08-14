package com.sxd.citic.cep.core.common

import java.sql.ResultSet

/**
  * Created by Administrator on 2018/8/4.
  */
object SQLConstants {
  val loadAppSQL="SELECT  a.app_id, a.ds_id,a.cn_id,a.app_name,k.kafka_hosts src_kafka_hosts,k.zokkeeper_host src_zookeeper_hosts,k.topic src_topic,k.groups src_groups,cf.kafka_hosts chl_kafka_hosts,cf.zokkeeper_host chl_zookeeper_hosts,cf.topic chl_topic,cf.groups chl_groups FROM im_app a INNER JOIN im_data_source_kafka k ON (a.ds_id = k.ds_id) INNER JOIN im_channel_kafka cf ON (a.cn_id = cf.cn_id) WHERE a.status=1 and a.app_id = ?"
  val loadSourceSQL="select field_id,ds_id,name,data_type,comment,sort_ sort, status from im_data_source_field where status=1 and ds_id=?"
  val loadChannelSQL="select field_id,cn_id,name,data_type,comment,sort_ srot,status from im_channel_field where status=1 and cn_id=?"
  val loadAppCepSQL="select cep_id,app_id,filed_name,expression from im_app_cep where app_id=?"
}
