package com.sxd.app_stm_001.scala.utils.thread

import com.sxd.app_stm_001.scala.utils.Constants
import org.springframework.http.HttpEntity
import org.springframework.web.client.RestTemplate

/**
  * Created by Administrator on 2018/8/12.
  */
class RestRequestRunnable(restTemplate: RestTemplate,httpEntity: HttpEntity[String]) extends Runnable{
  override def run(): Unit = {
    try {
      restTemplate.postForObject(Constants.REST_URL, httpEntity, classOf[String])
    }catch {
      case ex:Exception => ex.printStackTrace()
    }
  }
}
