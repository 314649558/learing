package com.bigdata.bdp.netty.quanweizhinan.ch11;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

/**
 * Created by Administrator on 2018/11/10.
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object>{

    private WebSocketServerHandshaker handshaker;

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        //如果是传统的HTTP请求
        if(msg instanceof FullHttpRequest){
            handleHttpRequest(ctx,(FullHttpRequest) msg);
        }else if(msg instanceof WebSocketFrame){  //webSocket请求接入
            handleWebSocketFrame(ctx,(WebSocketFrame) msg);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx,FullHttpRequest req){
        if(!req.getDecoderResult().isSuccess()
                || (!"websocket".equals(req.headers().get("Upgrade")))){
            sendHttpResponse(ctx,req,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        WebSocketServerHandshakerFactory wsFactory=new WebSocketServerHandshakerFactory(
                "ws://localhost:8080/websocket",null,false);

        handshaker=wsFactory.newHandshaker(req);

        if(handshaker==null){
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        }else{
            handshaker.handshake(ctx.channel(),req);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx,WebSocketFrame frame){
        //判断是否是关闭链路指令
        if(frame instanceof CloseWebSocketFrame){
            handshaker.close(ctx.channel(),(CloseWebSocketFrame) frame.retain());
            return;
        }

         //判断是否是Ping消息
        if(frame instanceof PingWebSocketFrame){
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        //本例程仅支持文本消息，不支持二进制消息
        if(!(frame instanceof TextWebSocketFrame)){
            throw new UnsupportedOperationException(String.format("%s frame types not supported",frame.getClass().getName()));
        }

        //返回应答消息
        String request=((TextWebSocketFrame) frame).text();

        ctx.channel().write(new TextWebSocketFrame(request+",欢迎使用Netty WebSocket服务，现在时刻: " + new java.util.Date().toString()));
    }
    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse resp){
        //返回给客户端应答
        if(resp.getStatus().code()!=200){
            ByteBuf buf= Unpooled.copiedBuffer(resp.getStatus().toString(), CharsetUtil.UTF_8);
            resp.content().writeBytes(buf);
            buf.release();
            HttpHeaders.setContentLength(resp, resp.content().readableBytes());
        }
        //如果是非Keep-Alive，关闭连接
        ChannelFuture f=ctx.channel().writeAndFlush(resp);
        if(!HttpHeaders.isKeepAlive(req) || resp.getStatus().code()!=200){
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
