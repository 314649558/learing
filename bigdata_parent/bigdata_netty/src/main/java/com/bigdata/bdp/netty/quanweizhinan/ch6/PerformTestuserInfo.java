package com.bigdata.bdp.netty.quanweizhinan.ch6;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2018/11/8.
 */
public class PerformTestuserInfo {


    public static void main(String[] args) throws Exception {
        UserInfoPoJo userInfoPoJo=new UserInfoPoJo();
        userInfoPoJo.buildUserId(100).buildUserName("hailong");
        int loop=10000000;
        ByteArrayOutputStream bos=null;
        ObjectOutputStream os=null;

        long startTime=System.currentTimeMillis();

        for(int i=0;i<loop;i++){
            bos=new ByteArrayOutputStream();
            os=new ObjectOutputStream(bos);
            os.writeObject(userInfoPoJo);
            os.flush();
            os.close();
            byte[] b=bos.toByteArray();
            bos.close();;
        }
        long endTime=System.currentTimeMillis();
        System.out.println("JDK 序列化耗时："+(endTime-startTime)+" ms");

        System.out.println("-------------------------------");

        ByteBuffer buffer=ByteBuffer.allocate(1024);
        startTime=System.currentTimeMillis();
        for(int i=0;i<loop;i++){
            byte[] b=userInfoPoJo.codec(buffer);
        }

        endTime=System.currentTimeMillis();
        System.out.println("byte array 序列化耗时："+(endTime-startTime)+" ms");

    }
}
