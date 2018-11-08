package com.bigdata.bdp.netty.quanweizhinan.ch3;

import com.bigdata.bdp.netty.quanweizhinan.NettyConstants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by Administrator on 2018/11/7.
 */
public class TimeServer {

    public void bind(int port) throws Exception{

        //配置服务端的NIO线程组
        EventLoopGroup boosGroup = new NioEventLoopGroup();   //用于对网络事件的监听处理
        EventLoopGroup workerGroop = new NioEventLoopGroup(); //用于数据的传输

        ServerBootstrap b=new ServerBootstrap();
        b.group(boosGroup,workerGroop)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,1024)
                .childHandler(new ChildChannelHandler());  //这个Handler是给boosGroup线程组使用的

        ChannelFuture f=b.bind(port).sync();

        f.channel().closeFuture().sync();  //等待服务端链路关闭之后进行退出

        boosGroup.shutdownGracefully();
        workerGroop.shutdownGracefully();
    }


    public static void main(String[] args) throws Exception{
        new TimeServer().bind(NettyConstants.PORT);
    }
    private class ChildChannelHandler extends ChannelInitializer{
        @Override
        protected void initChannel(Channel ch) throws Exception {
            ch.pipeline().addLast(new TimeServerHandler());
        }
    }
}
