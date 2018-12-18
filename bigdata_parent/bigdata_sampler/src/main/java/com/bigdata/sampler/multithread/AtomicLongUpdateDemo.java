package com.bigdata.sampler.multithread;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

/**
 * Created by Administrator on 2018/11/17.
 */
public class AtomicLongUpdateDemo {

    private static final AtomicLongFieldUpdater<AtomicLongUpdateDemo> TOTAL_PENDING_SIZE_UPDATE=
            AtomicLongFieldUpdater.newUpdater(AtomicLongUpdateDemo.class,"totalPendingSize");


    private static volatile long totalPendingSize=0;


    public static void main(String[] args) {

        TOTAL_PENDING_SIZE_UPDATE.compareAndSet(new AtomicLongUpdateDemo(),0,1);

        System.out.println(totalPendingSize);


    }


}
