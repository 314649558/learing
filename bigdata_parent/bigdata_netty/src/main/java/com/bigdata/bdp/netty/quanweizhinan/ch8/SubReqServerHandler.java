package com.bigdata.bdp.netty.quanweizhinan.ch8;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Administrator on 2018/11/9.
 */
public class SubReqServerHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeReqProto.SubscribeReq req=(SubscribeReqProto.SubscribeReq)msg;

        if("hailong".equalsIgnoreCase(req.getUserName())){
            System.out.println("Service accept client subscribe req :\n ["+req.toString()+"]");
            ctx.writeAndFlush(resp(req.getSubReqID()));
        }

    }

    private SubscribeRespProto.SubscribeResp resp(int subReqID) {
        SubscribeRespProto.SubscribeResp.Builder builder=SubscribeRespProto.SubscribeResp
                .newBuilder();

        builder.setSubReqID(subReqID);
        builder.setRespCode(0);
        builder.setDesc("Netty book order succeed, 3 days later,sent to then designated address");
        return builder.build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
