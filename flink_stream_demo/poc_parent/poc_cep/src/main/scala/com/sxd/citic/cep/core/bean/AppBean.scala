package com.sxd.citic.cep.core.bean

/**
  * Created by Administrator on 2018/8/4.
  */
case class AppBean(appId:String,
                   appName:String,
                   dsId:String,
                   srcKafkaHosts:String,
                   srcZookeeperHosts:String,
                   srcTopic:String,
                   srcGroups:String,
                   cnId:String,
                   chlKafkatHosts:String,
                   chlZookeeperHosts:String,
                   chlTopic:String,
                   chlGroups:String
                  )
