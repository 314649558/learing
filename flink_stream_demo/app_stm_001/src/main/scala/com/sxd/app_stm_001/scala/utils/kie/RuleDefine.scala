package com.sxd.app_stm_001.scala.utils.kie

/**
  *
  * 这里仅作测试，规则应该需要从数据库里面获取
  * Created by Administrator on 2018/8/4.
  */
object RuleDefine {


  val rule="package rules.dy; rule \"temp\" when eval(true) then System.out.println(\"动态规则被1出发咯\"); end"

}
