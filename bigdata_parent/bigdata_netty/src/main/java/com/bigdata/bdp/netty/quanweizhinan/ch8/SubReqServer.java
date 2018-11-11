package com.bigdata.bdp.netty.quanweizhinan.ch8;

import com.bigdata.bdp.netty.quanweizhinan.NettyConstants;
import com.bigdata.bdp.netty.quanweizhinan.ch3.TimeServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * Created by Administrator on 2018/11/9.
 */
public class SubReqServer {
    public void bind(int port) throws Exception{
        EventLoopGroup boosGroup = new NioEventLoopGroup();   //用于对网络事件的监听处理
        EventLoopGroup workerGroop = new NioEventLoopGroup(); //用于数据的传输

        ServerBootstrap b=new ServerBootstrap();
        b.group(boosGroup,workerGroop)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                        ch.pipeline().addLast(new ProtobufDecoder(SubscribeReqProto.SubscribeReq.getDefaultInstance()));
                        ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                        ch.pipeline().addLast(new ProtobufEncoder());
                        ch.pipeline().addLast(new SubReqServerHandler());
                    }
                });

        ChannelFuture f=b.bind(port).sync();

        f.channel().closeFuture().sync();  //等待服务端链路关闭之后进行退出

        boosGroup.shutdownGracefully();
        workerGroop.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        new SubReqServer().bind(NettyConstants.PORT);
    }
}
