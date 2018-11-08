package com.bigdata.bdp.netty.quanweizhinan.ch4.niaobaochaibao;

import com.bigdata.bdp.netty.quanweizhinan.NettyConstants;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Created by Administrator on 2018/11/7.
 */
public class Ch4TimeClient {

    public void connect(int port,String host) throws Exception{
        EventLoopGroup group=new NioEventLoopGroup();
        try{
            Bootstrap b=new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new Ch4TimeClientHandler());
                        }
                    });

            ChannelFuture f=b.connect(host,port).sync();

            f.channel().closeFuture().sync();
        }finally{
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception{
        new Ch4TimeClient().connect(NettyConstants.PORT,"localhost");
    }

}
