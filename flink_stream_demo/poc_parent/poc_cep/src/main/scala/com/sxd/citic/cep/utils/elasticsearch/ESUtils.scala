package com.sxd.citic.cep.utils.elasticsearch

import java.net.InetAddress
import java.util

import com.google.gson.JsonObject
import com.sxd.citic.cep.core.utils.{Constants, JsonUtils}
import org.apache.commons.lang3.StringUtils
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.index.query.{QueryBuilders, TermQueryBuilder}
import org.elasticsearch.transport.client.PreBuiltTransportClient

/**
  * Created by Administrator on 2018/8/7.
  */
object ESUtils {
  /**
    * 获取连接
    * @return
    */
  def getTransportClient():TransportClient={
    if(StringUtils.isNotEmpty(Constants.ES_ADDR)) {
      try {
        val settings: Settings = Settings.builder().put(Constants.ES_CLUSTER_NAME_KEY, Constants.ES_CLUSTER_NAME_VALUE).build()
        val client: TransportClient = new PreBuiltTransportClient(settings)
        val addrs:Array[String]=Constants.ES_ADDR.split(",")
        for(addr <- addrs){
          val host=addr.split(":")(0)
          val port=addr.split(":")(1).toInt
          client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host),port))
        }
        client
      }catch{
        case ex:Exception=>{
          ex.printStackTrace()
          null
        }
      }
    }else{
      null
    }
  }

  def close(client:TransportClient): Unit ={
    if(client!=null){
      client.close()
    }
  }


  def deleteIndex(client:TransportClient,indexName:String): Unit ={
    try {
      client.admin().indices().prepareDelete(indexName).execute().actionGet()
    }catch{
      case ex:Exception=>
    }
  }



  def writeDataES(client:TransportClient,lstData:util.List[JsonObject],indexName:String,indexType:String):Unit={

    if(lstData!=null && lstData.size()>0) {
      try {
        val bulkRequest = client.prepareBulk()
        for (i <- 0 until lstData.size()) {
          bulkRequest.add(client.prepareIndex(indexName, indexType).setSource(JsonUtils.toJson(lstData.get(i))))
        }
        bulkRequest.execute()
      } catch {
        case ex: Exception => ex.printStackTrace()
      }
    }
  }


  def main(args: Array[String]): Unit = {

    val client=this.getTransportClient()



    client.prepareSearch("").setTypes("").setQuery(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("bid","df549a89-de6d-48e1-a42d-4f0c126241d4")).must(QueryBuilders.termQuery("","")))



  }




}
