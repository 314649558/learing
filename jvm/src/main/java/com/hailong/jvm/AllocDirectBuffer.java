package com.hailong.jvm;

import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2019/7/18.
 *
 * 对比直接内存和Heap内存分配速度
 *
 */
public class AllocDirectBuffer {

    public void directAllocate(){
        long startTime=System.currentTimeMillis();

        for(int i=0;i<200000;i++){
            ByteBuffer b=ByteBuffer.allocateDirect(1000);  //分配直接内存
        }

        long endTime=System.currentTimeMillis();
        System.out.println("直接内存分配耗时:"+(endTime-startTime));
    }


    public void allocate(){
        long startTime=System.currentTimeMillis();

        for(int i=0;i<200000;i++){
            ByteBuffer b=ByteBuffer.allocate(1000);  //分配heap内存
        }

        long endTime=System.currentTimeMillis();

        System.out.println("Heap内存分配耗时:"+(endTime-startTime));
    }

    public static void main(String[] args) {
        AllocDirectBuffer allocDirectBuffer=new AllocDirectBuffer();

        allocDirectBuffer.directAllocate();
        allocDirectBuffer.allocate();
        System.out.println("---------");
        allocDirectBuffer.directAllocate();
        allocDirectBuffer.allocate();
    }
}
