package com.hailong.jvm.ref;

import java.util.HashMap;

/**
 * Created by Administrator on 2019/7/19.
 *
 * Stop-The-World  GC停顿
 *
 * -Xmx1g -Xms1g -Xmn512k -XX:+UseSerialGC -Xloggc:gc.log -XX:+PrintGCDetails
 */
public class StopWorldTest {

    public static class MyThread extends Thread{
        HashMap map=new HashMap();

        @Override
        public void run() {
            try {
                while (true) {
                    if (map.size() * 512 / 1024 / 1024 >= 900) {
                        map.clear();
                        System.out.println("map cleared");
                    }
                    byte[] b1;
                    for (int i = 0; i < 100; i++) {
                        b1 = new byte[512];
                        map.put(System.nanoTime(), b1);
                    }
                    Thread.sleep(1);
                }
            }catch (Exception e){

            }
        }
    }


    public static class PrintThread extends Thread{
        public static final long startTime=System.currentTimeMillis();

        @Override
        public void run() {
            try{
                while(true){
                    long t=System.currentTimeMillis();
                    System.out.println("t/1000"+"."+t%1000);
                    Thread.sleep(100);
                }
            }catch (Exception e){

            }
        }
    }

    public static void main(String[] args) {
        MyThread t=new MyThread();
        PrintThread p=new PrintThread();

        t.start();
        p.start();
    }

}
