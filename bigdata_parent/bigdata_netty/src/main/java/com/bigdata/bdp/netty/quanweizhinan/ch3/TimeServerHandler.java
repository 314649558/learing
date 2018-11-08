package com.bigdata.bdp.netty.quanweizhinan.ch3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Administrator on 2018/11/7.
 */
public class TimeServerHandler extends ChannelHandlerAdapter {

    /**
     * 读取客户端数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf=(ByteBuf) msg;
        byte[] req=new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body=new String(req,"UTF-8");
        System.out.println("The time server receive order :" + body);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new java.util.Date(System.currentTimeMillis()).toString():"BAD ORDER";
        ByteBuf resp= Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.write(resp);    //注意 为了性能的考虑，这里实际上不会直接写入到SocketChannel中并将数据发送给对方，它只是将其写入到缓冲区中
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();//将写入到SocketChannel中的消息发送给对方
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}

