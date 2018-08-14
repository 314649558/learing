package com.sxd.citic.drools.core.bean

import com.sxd.citic.drools.core.utils.Constants

/**
  * Created by Administrator on 2018/8/1.
  */
case class  FieldMappingBean(fieldName:String,dataType:String="",format:String="",filedMappingName:String="")


case class AppCep(cepId:String,appId:String,fieldName:String,expression:String)

case class RuleExpressionBean(filedName:String,expression:String,ruleType:String=Constants.EXPRESSION_RULE_TYPE.DEFAULT.toString)


case class DroolsBean(drlId:String,drlName:String,drlContent:String,isChange:String)