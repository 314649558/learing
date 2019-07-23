package com.hailong.jvm;

import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2019/7/23.
 *
 * -XX:+PrintGCDetails
 */
public class DirectBufferOOM {

    public static void main(String[] args) {
        for(int i=0;i<1024;i++){
            ByteBuffer.allocateDirect(10*1024*1024);
            System.out.println(i);
            //System.gc();
        }
    }

}
