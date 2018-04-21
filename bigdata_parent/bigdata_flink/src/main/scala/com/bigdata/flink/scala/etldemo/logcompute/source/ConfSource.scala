package com.bigdata.flink.scala.etldemo.logcompute.source

import com.bigdata.flink.scala.etldemo.logcompute.bean.ComputeConf
import javafx.scene.chart.NumberAxis.DefaultFormatter
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.slf4j.LoggerFactory

import scala.util.Try
import scalaj.http.Http
import org.json4s.DefaultFormats
import org.json4s.native.JsonMethods._

class ConfSource(url : String) extends SourceFunction[ComputeConf]{

  private val LOG=LoggerFactory.getLogger(classOf[ConfSource])

  @volatile private var isRunning: Boolean = true

  override def run(sourceContext: SourceFunction.SourceContext[ComputeConf]): Unit = {

    implicit val formats = DefaultFormatter

    while(true){
      Try{Http(url).timeout(2000,60000).asString}.toOption match {
        case Some(response) =>{
          response.code match {
            case 200 =>{
              parse(response.body).extractOpt[ComputeConf] match {
                case Some(conf) =>{
                  LOG.info("Pull configuration: {}",response.body)
                  sourceContext.collect(conf)
                }
              }
            }
            case _ => LOG.warn("invalid configuration:{}",response.body)
          }
        }
      }
      Thread.sleep(60000L)
    }


  }

  override def cancel(): Unit = {
    isRunning=false
  }
}
