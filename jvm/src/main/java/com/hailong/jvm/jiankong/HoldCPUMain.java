package com.hailong.jvm.jiankong;

/**
 * Created by Administrator on 2019/7/23.
 *
 * pidstat -p processid 1 3     
 */
public class HoldCPUMain {
    public static class HoldCPUTask implements Runnable{
        @Override
        public void run() {
            while (true){
                double a=Math.random()*Math.random();  //主要是为了占用CPU
            }
        }
    }

    public static class LazyTask implements Runnable{
        @Override
        public void run() {
            try {
                while(true) {
                    Thread.sleep(1000);   //一个空闲线程
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Thread(new HoldCPUTask()).start();
        new Thread(new LazyTask()).start();
        new Thread(new LazyTask()).start();
        new Thread(new LazyTask()).start();
    }
}
