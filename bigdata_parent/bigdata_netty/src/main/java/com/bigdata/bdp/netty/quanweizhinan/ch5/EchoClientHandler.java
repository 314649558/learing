package com.bigdata.bdp.netty.quanweizhinan.ch5;

import com.bigdata.bdp.netty.quanweizhinan.NettyConstants;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Administrator on 2018/11/8.
 */
public class EchoClientHandler extends ChannelHandlerAdapter{

    private int counter;

    private final String ECHO_REQ="Hi hailong,Welcome to Netty."+ NettyConstants.DELIMITER;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for(int i=0;i<20;i++){
            ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));
        }
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body=(String)msg;
        System.out.println("This is "+ ++counter+" times receive server :["+body+"]");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
