package com.bigdata.bdp.netty.quanweizhinan.ch22;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;

/**
 * Created by Administrator on 2018/11/18.
 */
public class PoolPerformanceDemo {


    private int loop=30000000;

    private String CONTET="Netty IO 高性能之道，内存池ByteBuf和普通ByteBuf内存性能对比";

    private void testPoolMemoryDemo(){
        long startTime=System.currentTimeMillis();
        ByteBuf poolBuffer=null;
        for(int i=0;i<loop;i++){
            poolBuffer= PooledByteBufAllocator.DEFAULT.directBuffer(1024);
            poolBuffer.writeBytes(CONTET.getBytes());
            poolBuffer.release();
        }
        long totalTime=System.currentTimeMillis()-startTime;

        System.out.println("线程池 bytebuf 耗时 :" + totalTime);
    }


    private void testComMemoryDemo(){
        long startTime=System.currentTimeMillis();
        ByteBuf buf=null;
        for(int i=0;i<loop;i++){
            buf= Unpooled.directBuffer(1024);
            buf.writeBytes(CONTET.getBytes());
            buf.release();
        }

        long totalTime=System.currentTimeMillis()-startTime;

        System.out.println("普通 bytebuf 耗时 :" + totalTime);
    }



    public static void main(String[] args) {

        PoolPerformanceDemo poolPerformanceDemo=new PoolPerformanceDemo();

        poolPerformanceDemo.testPoolMemoryDemo();
        System.out.println("-------------------------------------");
         poolPerformanceDemo.testComMemoryDemo();


    }



}
