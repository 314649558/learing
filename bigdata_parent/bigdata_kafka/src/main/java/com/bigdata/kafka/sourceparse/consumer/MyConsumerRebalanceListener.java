package com.bigdata.kafka.sourceparse.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;

/**
 * Created by Administrator on 2018/11/24.
 *
 * 消费者的回调函数
 *
 */
public class MyConsumerRebalanceListener implements ConsumerRebalanceListener {

    private Consumer<?,?> consumer;

    public MyConsumerRebalanceListener(Consumer<?,?> consumer){
        this.consumer=consumer;
    }


    //消费者停止拉去数据之后，Rebalance开始之前调用此方法
    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        for(TopicPartition partition: partitions){
            saveOffsetInExternalStore(consumer.position(partition));
        }
    }

    //Rebalance完成之后，消费者开始之前调用该方法
    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        for(TopicPartition partition: partitions){
            consumer.seek(partition,readOffsetFromExternalStore(partition));
        }
    }
    private void saveOffsetInExternalStore(long position){
        //TODO 保存offset到额外系统
        System.out.println("保存消费者的分区信息:"+position);
    }

    private long readOffsetFromExternalStore(TopicPartition partition){
        //TODO 从系统中获取offset
        System.out.println("从外部系统读取消费者的分区信息:"+partition.partition());
        return 0;
    }

}
