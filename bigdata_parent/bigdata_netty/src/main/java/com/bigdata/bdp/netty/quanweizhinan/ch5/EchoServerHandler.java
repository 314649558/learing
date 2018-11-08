package com.bigdata.bdp.netty.quanweizhinan.ch5;

import com.bigdata.bdp.netty.quanweizhinan.NettyConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Administrator on 2018/11/8.
 */
public class EchoServerHandler extends ChannelHandlerAdapter {

    private int counter=0;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body=(String)msg;
        System.out.println("This is "+ ++counter +" times receive client:["+body+"]");
        //将接收到的消息发送有发送给客户端，注意netty在 解析的时候回自动去掉指定的限制符，因此发送过去需要加上
        body+= NettyConstants.DELIMITER;
        ByteBuf buf= Unpooled.copiedBuffer(body.getBytes());
        ctx.writeAndFlush(buf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
