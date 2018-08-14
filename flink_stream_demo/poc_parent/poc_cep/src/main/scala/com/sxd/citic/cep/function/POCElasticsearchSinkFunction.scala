package com.sxd.citic.cep.function

import java.util

import com.sxd.citic.cep.core.utils.Constants
import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkFunction, RequestIndexer}
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.Requests

/**
  * Created by Administrator on 2018/8/7.
  */
class POCElasticsearchSinkFunction(map:util.Map[String,String]) extends ElasticsearchSinkFunction[String]{

  def createIndexRequest(element: String): IndexRequest = {
    Requests.indexRequest.index(map.get(Constants.ES_INDEX_KEY)).`type`(map.get(Constants.ES_TYPE_KEY)).source(element)
  }

  override def process(element: String, runtimeContext: RuntimeContext, requestIndexer: RequestIndexer): Unit = {
    requestIndexer.add(createIndexRequest(element))
  }
}
