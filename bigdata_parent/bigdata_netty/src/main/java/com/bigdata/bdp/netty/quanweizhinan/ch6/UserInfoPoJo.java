package com.bigdata.bdp.netty.quanweizhinan.ch6;


import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 *
 * 对象主要用来对比 JDK自带的ObjectInputStream和ObjectOutputStream 编码后和NIO中ButeBuffer编码后码流大小
 * Created by Administrator on 2018/11/8.
 */
public class UserInfoPoJo implements Serializable {
    private static final long serialVersionUID=1L;

    private String username;

    private int userID;

    public UserInfoPoJo buildUserName(String username){
        this.username=username;
        return this;
    }

    public UserInfoPoJo buildUserId(int userID){
        this.userID=userID;
        return this;
    }


    public final String getUsername(){
        return username;
    }

    public final int getUserId(){
        return userID;
    }


    public final void setUserID(int userID){
        this.userID=userID;
    }


    /**
     * 利用NIO的ByteBuffer进行编码
     * @return
     */
    public byte[] codec(){
        ByteBuffer buffer= ByteBuffer.allocate(1024);
        byte[] value=this.username.getBytes();
        buffer.putInt(value.length);
        buffer.put(value);
        buffer.putInt(this.userID);
        buffer.flip();
        value=null;
        byte[] result=new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }


    /**
     * 用于测试JDK自带的序列化测试性能
     * @return
     */
    public byte[] codec(ByteBuffer buffer){
        buffer.clear();
        byte[] value=this.username.getBytes();
        buffer.putInt(value.length);
        buffer.put(value);
        buffer.putInt(this.userID);
        buffer.flip();
        value=null;
        byte[] result=new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }


}
