package com.sxd.app_stm_002.work.utils

/**
  * Created by yuanhailong on 2018/7/30.
  */
object Constants {

  //字段中必须包含timestamp
  val TIMESTAMP = "timestamp"
  val TRUE = "true"
  val FALSE = "false"
  val serializer_value = "org.apache.kafka.common.serialization.StringSerializer"
  val deserializer_value = "org.apache.kafka.common.serialization.StringDeserializer"
  val MAX_REQUEST_SIZE_KEY = "max.request.size"
  val MAX_REQUEST_SIZE_VALUE = "30485760"
  val MAX_PARTITION_FETCH_BYTES_KEY = "max.partition.fetch.bytes"
  val MAX_PARTITION_FETCH_BYTES_VALUE = "30485760"
  val BOOTSTRAP_SERVERS_KEY = "bootstrap.servers"
  val ZOOKEEPER_CONNECT_KEY = "zookeeper.connect"
  val GROUP_ID_KEY = "group.id"
  val ES_CLUSTER_NAME_KEY = "cluster.name"
  //val ES_CLUSTER_NAME_VALUE = "es-cluster"
  val ES_CLUSTER_NAME_VALUE = "testcluster"
  val ES_INDEX_KEY = "esIndex"
  val ES_TYPE_KEY = "esType"
  val ES_URL_KEY = "esURL"

  //val ES_ADDR="192.168.132.215:9300,192.168.132.216:9300,192.168.132.217:9300"
  val ES_ADDR="192.168.112.101:9300"
  //val ES_ADDR=":9300"
  //规则类型
  object RULE_TYPE extends Enumeration {
    type TYPE = Value
    val UNDEFINED = Value(1)
    val DROOLS = Value(2)
    val MODE_ALG = Value(3)
  }

  //数据源类型
  object SOURCE_TYPE extends Enumeration {
    type TYPE = Value
    val KAFKA = Value("KAFKA")
    val LOG = Value("LOG")
    val MYSQL = Value("MYSQL")
    val ORACLE = Value("ORACLE")
  }

  //数据源类型
  object EXPRESSION_RULE_TYPE extends Enumeration {
    type TYPE = Value
    val DEFAULT = Value("")
    val FILTER = Value("FILTER")
  }




}
