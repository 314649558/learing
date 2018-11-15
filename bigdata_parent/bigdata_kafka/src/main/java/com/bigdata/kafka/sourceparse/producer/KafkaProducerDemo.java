package com.bigdata.kafka.sourceparse.producer;

import com.bigdata.kafka.sourceparse.KafkaConstants;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/11/15.
 */
public class KafkaProducerDemo {

    // kafka-console-producer.sh --broker-list 192.168.112.100:9092 --topic testhl
    // kafka-console-consumer.sh --bootstrap-server 192.168.112.100:9092 --topic testhl

    static String topic="testhl";


    public static void main(String[] args) {

        Map<String,Object> kafkaParam=new HashMap<>();

        kafkaParam.put("bootstrap.servers", KafkaConstants.KAFKA_BOOTSTRAP_SERVER);
        kafkaParam.put("key.serializer", KafkaConstants.KAFKA_SERIALIZER);
        kafkaParam.put("value.serializer", KafkaConstants.KAFKA_SERIALIZER);
        kafkaParam.put("interceptor.classes", "com.bigdata.kafka.sourceparse.producer.MyProducerInterceptor");

        KafkaProducer<String,String> kafkaProducer=new KafkaProducer<String, String>(kafkaParam);

        String msg="hailong test kafka interceptor";

        ProducerRecord<String,String> record=new ProducerRecord<String, String>(topic,"",msg);

        kafkaProducer.send(record);
        kafkaProducer.close();



    }

}
