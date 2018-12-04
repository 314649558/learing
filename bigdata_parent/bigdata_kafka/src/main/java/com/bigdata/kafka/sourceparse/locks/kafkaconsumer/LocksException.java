package com.bigdata.kafka.sourceparse.locks.kafkaconsumer;

/**
 * Created by Administrator on 2018/12/4.
 */
public class LocksException extends RuntimeException {
    public LocksException(){

    }


    public LocksException(String msg){
        System.out.println(msg);
    }
}
