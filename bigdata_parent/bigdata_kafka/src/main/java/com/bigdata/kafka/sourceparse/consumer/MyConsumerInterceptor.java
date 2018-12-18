package com.bigdata.kafka.sourceparse.consumer;

import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.*;

/**
 * Created by Administrator on 2018/11/25.
 */
public class MyConsumerInterceptor implements ConsumerInterceptor<String,String> {
    @Override
    public ConsumerRecords<String, String> onConsume(ConsumerRecords<String, String> records) {
        //TODO 消费者在获取消息前可以对消息进行处理
        Map<String,List<ConsumerRecord<String,String>>> retMap=new HashMap<>();

        for (ConsumerRecord<String,String> record:records) {
            String topicName=record.topic();
            List<ConsumerRecord<String,String>> recordLst=null;
            if(retMap.get(topicName)==null || retMap.get(topicName).size()==0){
                recordLst=new ArrayList<>();
            }else{
                recordLst=retMap.get(topicName);
            }
            recordLst.add(new ConsumerRecord<String, String>(record.topic(),record.partition(),record.offset(),record.key(),"消息被我拦截到了:"+record.value()));
            retMap.put(topicName,recordLst);
        }



        return new ConsumerRecords(retMap);
    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> offsets) {
        //TODO 消费者在提交offset之前可以对其进行处理
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
