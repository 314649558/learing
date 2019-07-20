package com.hailong.jvm;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/7/20.
 *
 *对象何时进入老年代  大对象分配
 *
 * -Xmx32m -Xms32m -XX:+UseSerialGC -XX:+PrintGCDetails -XX:PretenureSizeThreshold=1000 -XX:-UseTLAB
 */
public class PretenureSizeThreshold {

    public static final int _1K=1024;

    public static void main(String[] args) {
        Map<Integer,byte[]> map=new HashMap<>();


        for(int i=0;i<5 * _1K;i++){
            byte[] b =new byte[_1K];
            map.put(i,b);
        }




    }

}
