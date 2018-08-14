package com.sxd.citic.drools.utils.http

import com.google.gson.JsonObject
import com.sxd.citic.drools.core.utils.JsonUtils
import org.apache.http.HttpHost
import org.apache.http.client.methods.{CloseableHttpResponse, HttpPost}
import org.apache.http.conn.routing.HttpRoute
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.{CloseableHttpClient, HttpClients}
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager

/**
  * Created by Administrator on 2018/8/13.
  */
object HttpUtils {

  def getHttpClient(requestUrl:String): Option[CloseableHttpClient] ={
    try {
      val cm: PoolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager()
      cm.setMaxTotal(200)
      cm.setDefaultMaxPerRoute(20)
      val http: HttpHost = new HttpHost(URLParseUtils.parseUrl(requestUrl,"HOST"),URLParseUtils.parseUrl(requestUrl,"PORT").toInt)
      cm.setMaxPerRoute(new HttpRoute(http), 50)
      Some(HttpClients.custom().setConnectionManager(cm).build())
    }catch {
      case ex:Exception=> None
    }
  }
  def close(httpClient: CloseableHttpClient): Unit ={
    if(httpClient!=null){
      httpClient.close()
    }
  }
  /**
    * 发送数据
    * @param httpClient
    * @param uri
    */
  def sendRequest(httpClient:CloseableHttpClient,uri:String,data:String): Boolean ={
    var response: CloseableHttpResponse= null
    try {
      val stringEntity: StringEntity = new StringEntity(data)
      stringEntity.setContentType("text/json")
      val httpPost=new HttpPost(uri)
      httpPost.setHeader("Connection","keep-alive")
      httpPost.setHeader("Accept","application/json; charset=UTF-8")
      httpPost.setEntity(stringEntity)
      response = httpClient.execute(httpPost)
      if(response.getStatusLine.getStatusCode!=200){
        println("http连接异常")
        false
      }else{
        true
      }
    }catch {
      case ex:Exception=>{
        ex.printStackTrace()
        false
      }
    }finally {
      if(response!=null) response.close()
    }
  }

  def main(args: Array[String]): Unit = {

    val uri="http://localhost:8100/data"

    val httpClient: Option[CloseableHttpClient] =getHttpClient(uri)

    val json=new JsonObject

    json.addProperty("name","hailong")
    sendRequest(httpClient.get,uri,JsonUtils.toJson(json))


  }
}
