该项目是本人在中信银行项目竞标时 个人根据场景开发的项目，该项目采用flink 技术做实时数据处理
其中
1 poc_parent 目录下有两个子项目
   1 poc_cep 使用flink cep 技术做复杂时间处理
   2 poc_drools flink 结合 drools 做复杂规则处理
2 app_stm_001 是poc_drools 是提炼出来 结合了 spring boot      
3 app_stm_002 是poc_cep 是poc_cep 提炼出来结合 spring boot

本POC项目所使用的主要技术
1 ELASTICSEARCH 用于做数据统计
2 FLINK 做实事分析处理
3 MYSQL 元数据存储管理
4 DROOLS是 规则处理和维护
5 KAFKA 消息队列


FLINK ON YARN 部署步骤

1 启动一个yarn应用
yarn-session.sh -jm 2048 -n 2 -s 4 -nm testflink -tm 2048 -d
执行完这个命令后会在yarn上有一个应用
2 启动flink 应用
flink run -j poc_drools-1.0-SNAPSHOT.jar -c com.sxd.citic.drools.main.AppWork -p 6 -yid application_1533530382764_0001


kafka 命令
查看topic 列表
kafka-topics.sh --list --zookeeper 192.168.112.101:2181
查看topic 详情
kafka-topics.sh --zookeeper 192.168.112.101:2181 --topic pocsrc --describe
创建topic 
kafka-topics.sh --zookeeper 192.168.112.101:2181 --create --replication-factor 1 --partitions 2 --topic pocsrc
生产数据
kafka-console-producer.sh --broker-list 192.168.112.101:9092 --topic pocsrc11
消费数据
kafka-console-consumer.sh --bootstrap-server 192.168.112.101:9092 --topic pocsrc1


ELASTICSEARCH 
在ES5.X之上 无法对text的数据做聚合 需要修改 mapping
如下
curl -XPUT 'host:ip/indexName/typeName/_mapping' -d
'
{
  "properties": {
    "storeNbr": {
      "type": "text",
      "fielddata": true
    }
  }
}
'


