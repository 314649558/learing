package com.bigdata.bdp.netty.quanweizhinan.ch4.niaobaochaibao;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Administrator on 2018/11/7.
 */
public class Ch4TimeServerHandler extends ChannelHandlerAdapter {

    private int counter;

    /**
     * 读取客户端数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body=(String)msg;
        System.out.println("The time server receive order :" + body  +"       the counter is :" + ++counter);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new java.util.Date(System.currentTimeMillis()).toString():"BAD ORDER";
        currentTime=currentTime+System.getProperty("line.separator");
        ByteBuf resp= Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.writeAndFlush(resp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}

