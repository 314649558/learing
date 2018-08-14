package com.sxd.app_stm_001.scala.function

import com.sxd.app_stm_001.scala.utils.{Constants, JsonUtils}
import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkFunction, RequestIndexer}
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.Requests

/**
  * Created by Administrator on 2018/8/7.
  */
class POCElasticsearchSinkFunction(config:Map[String,String]) extends ElasticsearchSinkFunction[String]{

  def createIndexRequest(element: String): IndexRequest = {
    Requests.indexRequest.index(config.get(Constants.ES_INDEX_KEY).get).`type`(config.get(Constants.ES_TYPE_KEY).get).source(element)
  }

  override def process(element: String, runtimeContext: RuntimeContext, requestIndexer: RequestIndexer): Unit = {



    requestIndexer.add(createIndexRequest(element))
  }
}
