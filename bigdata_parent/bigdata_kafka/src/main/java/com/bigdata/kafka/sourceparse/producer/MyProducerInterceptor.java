package com.bigdata.kafka.sourceparse.producer;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

/**
 * Created by Administrator on 2018/11/15.
 */
public class MyProducerInterceptor implements ProducerInterceptor<String,String> {
    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {

        if(record.value().contains("hailong")){
            return new ProducerRecord<String, String>(record.topic(),record.key(),"MyProducerInterceptor:"+record.value());
        }else {
            return new ProducerRecord<String, String>(record.topic(),record.key(),"");
        }
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if(metadata!=null){
            System.out.println("topic name is:"+metadata.topic());
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
