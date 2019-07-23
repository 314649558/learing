package com.hailong.jvm;

/**
 * Created by Administrator on 2019/7/23.
 * -Xmx1g
 */
public class MutilThreadOOM {

    public static class SleepThread implements Runnable{
        @Override
        public void run() {
            try {
                Thread.sleep(100000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        for(int i=0;i<100000;i++){
            new Thread(new SleepThread()).start();
            System.out.println("Thread"+i+" created");
        }
    }



}
