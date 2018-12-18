package com.bigdata.sampler.multithread;

import java.util.concurrent.TimeUnit;

/**
 * Volatile 关键字有如下两个功能：
 * 1 线程可见性：当一个线程修改了volatile修饰的变量之后，无论是否加锁，线程都可以立即看到最新的修改
 * 2 禁止指令重排序：
 *
 * 经验总结：
 *    1 volatile 不能替代锁，因为它仅仅解决了可见性问题，并不能解决多线程并发下互斥的问题
 *    2 volatile 最适合一个线程写，其他线程读的场合
 */
public class VolatileKeyDemo {


    private static boolean stop;


    public static void main(String[] args) throws InterruptedException {
        Thread workThread=new Thread(new Runnable() {
            @Override
            public void run() {
                int i=0;
                while (!stop){
                    i++;
                    System.out.println(i);
                    try{
                        TimeUnit.SECONDS.sleep(1);
                    }catch (InterruptedException e){
                    }
                }
            }
        });
        workThread.start();
        TimeUnit.SECONDS.sleep(3);
        stop=true;
    }

}
