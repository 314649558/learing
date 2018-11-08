package com.bigdata.bdp.netty.quanweizhinan.ch5;

import com.bigdata.bdp.netty.quanweizhinan.NettyConstants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.Charset;

/**
 * Created by Administrator on 2018/11/8.
 */
public class EchoServer {

    public void bind(int port) throws Exception{
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup wokerGroup=new NioEventLoopGroup();
        try{
            ServerBootstrap b=new ServerBootstrap();
            b.group(bossGroup,wokerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ByteBuf buf= Unpooled.copiedBuffer(NettyConstants.DELIMITER.getBytes());
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,buf));
                            ch.pipeline().addLast(new StringDecoder(Charset.defaultCharset()));
                            ch.pipeline().addLast(new EchoServerHandler());
                        }
                    });
            ChannelFuture f=b.bind(port).sync();
            f.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            wokerGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) throws Exception{
        new EchoServer().bind(NettyConstants.PORT);
    }
}
