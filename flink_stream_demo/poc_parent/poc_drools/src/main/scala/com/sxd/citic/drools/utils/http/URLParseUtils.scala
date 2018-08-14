package com.sxd.citic.drools.utils.http

import java.net.URL

import org.apache.commons.lang3.StringUtils


/**
  * Created by Administrator on 2018/8/13.
  */
object URLParseUtils {



  def parseUrl(urlStr:String,partToExtract:String): String ={

    if (StringUtils.isEmpty(urlStr)|| StringUtils.isEmpty(partToExtract)){
        ""
    }else{
      try {
        val url = new URL(urlStr)
        if (partToExtract.equalsIgnoreCase("HOST")) {
          url.getHost
        }else if (partToExtract.equalsIgnoreCase("PATH")){
          url.getPath
        }else if (partToExtract.equalsIgnoreCase("PORT")){
          if(url.getPort == -1){
            "80"
          }else{
            url.getPort.toString
          }
        }else if (partToExtract.equalsIgnoreCase("QUERY")) {
          url.getQuery
        }
        else if (partToExtract.equalsIgnoreCase("REF")) {
          url.getRef
        }
        else if (partToExtract.equalsIgnoreCase("PROTOCOL")){
          url.getProtocol
        }
        else if (partToExtract.equalsIgnoreCase("FILE")){
          url.getFile
        }
        else if (partToExtract.equalsIgnoreCase("AUTHORITY")) {
          url.getAuthority
        }
        else if (partToExtract.equalsIgnoreCase("USERINFO")) {
          url.getUserInfo
        }
        else {
          ""
        }

      }catch {
        case ex:Exception=>{
          ""
        }
      }
    }

  }



  def main(args: Array[String]): Unit = {
    println(URLParseUtils.parseUrl("http://localhost:8100/data","HOST"))
    println(URLParseUtils.parseUrl("http://localhost:8100/data","PORT"))
  }

}
