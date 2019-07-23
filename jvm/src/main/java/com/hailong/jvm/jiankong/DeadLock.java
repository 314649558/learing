package com.hailong.jvm.jiankong;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2019/7/23.
 */
public class DeadLock extends Thread{
    protected Object myDirect;

    static ReentrantLock south=new ReentrantLock();
    static ReentrantLock north=new ReentrantLock();

    public DeadLock(Object obj){
        this.myDirect=obj;

        if(myDirect==south){
            this.setName("south");
        }

        if(myDirect==north){
            this.setName("north");
        }
    }

    @Override
    public void run() {
        if(myDirect==south){
            try {
                north.lockInterruptibly();  //占用north
                try{
                    Thread.sleep(500);
                }catch (Exception e){
                    e.printStackTrace();
                }
                south.lockInterruptibly();  //south
                System.out.println("car to south has passed");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                if(north.isHeldByCurrentThread()){
                    north.unlock();
                }
                if(south.isHeldByCurrentThread()){
                    south.unlock();
                }
            }
        }


        if(myDirect==north){
            try {
                south.lockInterruptibly();  //south
                try{
                    Thread.sleep(500);
                }catch (Exception e){
                    e.printStackTrace();
                }
                north.lockInterruptibly();  //north
                System.out.println("car to north has killed");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                if(north.isHeldByCurrentThread()){
                    north.unlock();
                }
                if(south.isHeldByCurrentThread()){
                    south.unlock();
                }
            }
        }
    }


    public static void main(String[] args) {
        DeadLock car2south=new DeadLock(south);
        DeadLock car2north=new DeadLock(north);
        car2north.start();
        car2south.start();

    }
}
