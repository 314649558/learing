package com.sxd.citic.cep.utils
import javax.script.ScriptEngineManager

import com.google.gson.JsonObject
import com.sxd.citic.cep.core.utils.JsonUtils
import org.apache.commons.lang3.StringUtils
/**
  * Created by Administrator on 2018/8/2.
  */
object ExpressionUtils {
  val engine=new ScriptEngineManager().getEngineByName("js")
  def expressionParse(expression:String,jsonObject :JsonObject):String={
    var expr=expression
    if(StringUtils.isNotEmpty(expression) && jsonObject!=null){
      val pattern="\\W*(\\w+)[^\\w\\r\\n]+"
      expression.replaceAll(pattern,"$1,").split(",").foreach(f=>{
        try {
          val v = jsonObject.get(f).getAsString
          expr = expr.replace(f, v)
        }catch{
          case ex:Exception=>{
            ex.printStackTrace()
          }
        }
      })
    }
    val result=engine.eval(expr).toString
    result
  }

  def main(args: Array[String]): Unit = {
    val json="{\"item_nbr\":1006,\"trade_date\":\"20180729\",\"store_nbr\":1,\"cost_amt\":78,\"net_sales_amt\":85,\"retail_amt\":289.0}"
    val jsonObject=JsonUtils.strToJson(json)
    val expression="(retail_amt-cost_amt)/retail_amt > 1000"
    println(ExpressionUtils.expressionParse(expression,jsonObject))
  }

}
