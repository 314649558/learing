package com.sxd.citic.cep.core.bean

import com.sxd.citic.cep.core.utils.Constants


/**
  * Created by Administrator on 2018/8/1.
  */
case class  FieldMappingBean(fieldName:String,dataType:String="",format:String="",filedMappingName:String="")

case class AppCep(cepId:String,appId:String,fieldName:String,expression:String)
case class RuleExpressionBean(filedName:String,expression:String,ruleType:String=Constants.EXPRESSION_RULE_TYPE.DEFAULT.toString)
