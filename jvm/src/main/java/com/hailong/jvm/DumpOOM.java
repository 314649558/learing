package com.hailong.jvm;

import java.util.Vector;

/**
 * Created by Administrator on 2019/7/18.
 *
 * -Xmx20m -Xms5m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=D:/dumpoom.dump
 */
public class DumpOOM {
    public static void main(String[] args) {
        Vector v=new Vector();

        for(int i=0;i<25;i++){
            v.add(new byte[1*1024*1024]);  //分配heap的内存
        }


    }
}
