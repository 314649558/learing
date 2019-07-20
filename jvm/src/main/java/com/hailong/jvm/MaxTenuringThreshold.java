package com.hailong.jvm;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/7/20.
 *
 * 对象何时进入老年代
 *
 * 根据GC次数来进入
 *
 *
 * -Xmx1024M -Xms1024M -XX:+PrintGCDetails -XX:MaxTenuringThreshold=15 -XX:+PrintHeapAtGC
 */
public class MaxTenuringThreshold {

    public static final int _1M=1024*1024;
    public static final int _1K=1024;

    public static void main(String[] args) {

        Map<Integer,byte[]> map=new HashMap<>();

        for(int i=0;i<5 * _1K;i++){
            byte[] b =new byte[_1K];
            map.put(i,b);
        }


        for(int i=0;i<5 * 17;i++){
            for(int k=0;k<270;k++){
                byte[] b=new byte[_1M];
            }
        }





    }


}
