package com.hailong.jvm;

import java.util.Random;

/**
 * Created by Administrator on 2019/7/21.
 */
public class HoldLockMail {

    public static Object[] lock=new Object[10];

    public static Random r=new Random();

    static{
        for(int i=0;i<lock.length;i++){
            lock[i]=new Object();
        }
    }


    public static class HoldLockTask implements Runnable{

        private  int i;

        public HoldLockTask(int i){
            this.i=i;
        }

        @Override
        public void run() {
            try{
                while (true){

                    synchronized (lock[i]){
                        if(i%2==0){
                            lock[i].wait(r.nextInt(10));
                        }else{
                            lock[i].notifyAll();
                        }
                    }
                    Thread.sleep(1);
                }
            }catch (Exception e){

            }
        }
    }

    public static void main(String[] args) {
        for(int i=0;i<lock.length*2 ;i++){
            new Thread(new HoldLockTask(i/2)).start();  //每两个线程使用同一个锁对象
        }
    }



}
