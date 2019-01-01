package com.bigdata.sampler.lock.bingfabiancheng.locks.InnerLocks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yuanhailong on 2018/12/31.
 *
 * 内部锁：每个Java对象都可以隐式的扮演内部锁的角色
 *
 * 进入内部锁的唯一方式是保护的同步代码块
 *
 */
public class InnerLocksTest {

    public AtomicInteger state=new AtomicInteger(1);

    /**
     * synchronized 包括的代码块是内部锁  ，其中锁的对象隐式采用的是InnerLocksTest
     */
    public synchronized void testInnerLocks(InnerLocksCallBack callBack){
        try{

            callBack.beforeCallBack();

            System.out.println("current state is :"+state.get());

            if (!state.compareAndSet(1,0)){
                System.out.println("waing ....");
                TimeUnit.SECONDS.sleep(10);
            }

            callBack.afterCallBack();

            System.out.println("execute finish");
        }catch (Exception e){

        }
    }


    public static void main(String[] args) {

        final InnerLocksTest innerLocksTest=new InnerLocksTest();

        new Thread(new Runnable() {
            @Override
            public void run() {

                int val=innerLocksTest.state.incrementAndGet();

                innerLocksTest.testInnerLocks(new InnerLocksCallBack() {
                    @Override
                    public void beforeCallBack() {
                        System.out.println("start:" + System.currentTimeMillis());
                        System.out.println(Thread.currentThread().getName());
                    }

                    @Override
                    public void afterCallBack() {
                        System.out.println("end:" + System.currentTimeMillis());
                    }
                });
                //状态还原
                innerLocksTest.state.set(1);
            }
        }).start();
    }
}


interface InnerLocksCallBack{

    public void beforeCallBack();

    public void afterCallBack();
}
