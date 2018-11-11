package com.bigdata.bdp.netty.quanweizhinan.ch12.codec;

import com.bigdata.bdp.netty.quanweizhinan.ch12.struct.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Administrator on 2018/11/10.
 */
public class NettyMessageEncoder extends MessageToByteEncoder<NettyMessage> {


    MarshallingEncoder marshallingEncoder;


    public NettyMessageEncoder() throws IOException{
        this.marshallingEncoder = new MarshallingEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, ByteBuf sendBuf) throws Exception {

        if(msg==null || msg.getHeader()==null){
            throw new Exception("The encode message is null");
        }

        //消息头
        {
            sendBuf.writeInt(msg.getHeader().getCrcCode());
            sendBuf.writeInt(msg.getHeader().getLength());
            sendBuf.writeLong(msg.getHeader().getSessionID());
            sendBuf.writeByte(msg.getHeader().getType());
            sendBuf.writeByte(msg.getHeader().getPriority());
            sendBuf.writeInt(msg.getHeader().getAttachement().size());
        }


        //附件消息
        {
            String key = null;
            byte[] keyArray = null;
            Object value = null;
            for (Map.Entry<String, Object> param : msg.getHeader().getAttachement().entrySet()) {
                key = param.getKey();
                keyArray = key.getBytes("UTF-8");
                sendBuf.writeInt(keyArray.length);
                sendBuf.writeBytes(keyArray);
                value = param.getValue();
                marshallingEncoder.encode(value, sendBuf);
            }
            key = null;
            keyArray = null;
            value = null;
        }

        //消息体
        {
            if (msg.getBody() != null) {
                marshallingEncoder.encode(msg.getBody(), sendBuf);
            } else {
                sendBuf.writeByte(0);
            }
        }
        sendBuf.setInt(4,sendBuf.readableBytes()-8);
    }
}
