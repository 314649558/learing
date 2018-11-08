package com.bigdata.bdp.netty.quanweizhinan.ch3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Administrator on 2018/11/7.
 */
public class TimeClientHandler extends ChannelHandlerAdapter{
    private final ByteBuf firstMessage;

    public TimeClientHandler(){
        byte[] req="QUERY TIME ORDER".getBytes();
        firstMessage= Unpooled.copiedBuffer(req);
    }


    /**
     * connection 建立之后，就会调用此方法，他会将数据写入到服务端
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(firstMessage);
    }


    /**
     * 读取服务端返回的数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf=(ByteBuf)msg;
        byte[] req=new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body=new String(req,"UTF-8");
        System.out.println("Now is :" +body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        ctx.close();
    }
}
