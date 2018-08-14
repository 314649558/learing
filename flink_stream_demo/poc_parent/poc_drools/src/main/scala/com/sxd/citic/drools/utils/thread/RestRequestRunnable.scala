package com.sxd.citic.drools.utils.thread

import com.sxd.citic.drools.utils.http.HttpUtils
import org.apache.http.impl.client.CloseableHttpClient

/**
  * Created by Administrator on 2018/8/12.
  */
class RestRequestRunnable(httpClient:CloseableHttpClient,uri:String,data:String) extends Runnable{
  override def run(): Unit = {
    try {

      HttpUtils.sendRequest(httpClient,uri,data)
    }catch {
      case ex:Exception => ex.printStackTrace()
    }
  }
}
