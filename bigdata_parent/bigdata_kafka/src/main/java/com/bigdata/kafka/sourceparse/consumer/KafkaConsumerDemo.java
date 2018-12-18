package com.bigdata.kafka.sourceparse.consumer;

import com.bigdata.kafka.sourceparse.KafkaConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by Administrator on 2018/11/24.
 */
public class KafkaConsumerDemo {

    public static void main(String[] args) {

        Properties props=new Properties();
        props.put("bootstrap.servers", KafkaConstants.KAFKA_BOOTSTRAP_SERVER);
        props.put("group.id","test");
        props.put("enable.auto.commit","true");//这种方式提交offset无法保证精确一次语义
        props.put("auto.commit.interval.ms","1000");//offset提交频率
        props.put("session.timeout.ms","30000");
        props.put("key.deserializer",KafkaConstants.KAFKA_DESERIALIZER);
        props.put("value.deserializer",KafkaConstants.KAFKA_DESERIALIZER);
        props.put("interceptor.classes","com.bigdata.kafka.sourceparse.consumer.MyConsumerInterceptor");

        KafkaConsumer<String,String> consumer=new KafkaConsumer<String, String>(props);
        //订阅topic
        consumer.subscribe(Arrays.asList(KafkaConstants.TOPIC)/*,new MyConsumerRebalanceListener(consumer)*/);
        try{
            while(true){
                //从服务端获取消息，一次可以获取多条
                ConsumerRecords<String,String> records=consumer.poll(100);
                for (ConsumerRecord record:records){
                    System.out.printf("offset = %d, key = %s , value = %s\n",record.offset(),record.key(),record.value());
                }
            }
        }finally {
            consumer.close();
        }


    }
}
