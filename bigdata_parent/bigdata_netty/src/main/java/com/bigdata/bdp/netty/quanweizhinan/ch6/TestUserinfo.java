package com.bigdata.bdp.netty.quanweizhinan.ch6;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Administrator on 2018/11/8.
 */
public class TestUserinfo {

    public static void main(String[] args) throws Exception {
        UserInfoPoJo infoPoJo=new UserInfoPoJo();
        infoPoJo.buildUserId(100).buildUserName("hailong");
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        ObjectOutputStream os=new ObjectOutputStream(bos);
        os.writeObject(infoPoJo);
        os.flush();
        os.close();

        byte[] b=bos.toByteArray();
        System.out.println("JDK 序列化后长度是:"+b.length);
        bos.close();

        System.out.println("-------------------------");

        System.out.println("byte array 序列化长度后是："+infoPoJo.codec().length);

    }
}
