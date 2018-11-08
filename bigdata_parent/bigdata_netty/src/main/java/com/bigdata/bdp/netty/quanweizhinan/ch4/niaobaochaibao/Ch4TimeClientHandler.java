package com.bigdata.bdp.netty.quanweizhinan.ch4.niaobaochaibao;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Administrator on 2018/11/7.
 */
public class Ch4TimeClientHandler extends ChannelHandlerAdapter{
    private int counter;

    private byte[] req;

    public Ch4TimeClientHandler(){
        req=("QUERY TIME ORDER"+System.getProperty("line.separator")).getBytes();
        //req="QUERY TIME ORDER".getBytes();
    }


    /**
     * connection 建立之后，就会调用此方法，他会将数据写入到服务端
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf message=null;
        for(int i=0;i<100;i++){
            message=Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }
    }


    /**
     * 读取服务端返回的数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String body=(String) msg;
        System.out.println("Now is :" +body+"  ; the counter is  : " + ++counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        ctx.close();
    }
}
