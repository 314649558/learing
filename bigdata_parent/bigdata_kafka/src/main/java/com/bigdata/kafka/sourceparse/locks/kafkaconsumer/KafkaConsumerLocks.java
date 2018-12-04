package com.bigdata.kafka.sourceparse.locks.kafkaconsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Administrator on 2018/12/4.
 */
public class KafkaConsumerLocks {


    private static final long NO_CURRENT_THREAD = -1L;
    private final AtomicLong currentThread = new AtomicLong(NO_CURRENT_THREAD);
    private final AtomicInteger refcount = new AtomicInteger(0);

    private List<String> dataLst=new ArrayList<>();

    public List<String> getDataLst() {
        return dataLst;
    }

    private void acquire(){
        long threadId=Thread.currentThread().getId();
        System.out.println("--------------acquire------------");
        //判断线程ID是否是当前线程ID
        if(threadId != currentThread.get() && !currentThread.compareAndSet(NO_CURRENT_THREAD,1)){
            throw new LocksException("多线程并发异常");
        }
        refcount.incrementAndGet();
    }


    private void release() {
        System.out.println("--------------release------------");
        if (refcount.decrementAndGet() == 0)
            currentThread.set(NO_CURRENT_THREAD);
    }

    public void putDataLst(String msg){
        acquire();
        try{
            dataLst.add(msg);
        }finally {
            release();
        }
    }


    public static void main(String[] args) {
        for(int i=0;i<100;i++){
            new Thread(new Runnable() {

                KafkaConsumerLocks locks=new KafkaConsumerLocks();
                @Override
                public void run() {
                    locks.putDataLst(Thread.currentThread().getName());
                }
            }).start();
        }




    }





}
