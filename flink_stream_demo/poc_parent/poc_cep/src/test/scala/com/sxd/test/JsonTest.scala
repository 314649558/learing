package com.sxd.test

import com.sxd.citic.cep.core.utils.JsonUtils

/**
  * Created by Administrator on 2018/8/1.
  */
object JsonTest {

  def main(args: Array[String]): Unit = {


    val json="[{\"item_nbr\":1006,\"trade_date\":\"20180729\",\"store_nbr\":1,\"cost_amt\":78,\"net_sales_amt\":85,\"retail_amt\":289},{\"item_nbr\":1006,\"trade_date\":\"20180729\",\"store_nbr\":1,\"cost_amt\":78,\"net_sales_amt\":85,\"retail_amt\":289}]"



    val arr=JsonUtils.strToJsonArray(json)






  }


}
