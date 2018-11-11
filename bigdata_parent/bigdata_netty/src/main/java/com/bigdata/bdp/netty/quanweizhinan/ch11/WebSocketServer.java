package com.bigdata.bdp.netty.quanweizhinan.ch11;

import com.bigdata.bdp.netty.quanweizhinan.NettyConstants;
import com.bigdata.bdp.netty.quanweizhinan.ch10.HttpFileServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Created by Administrator on 2018/11/10.
 */
public class WebSocketServer {


    public void run(final int port) throws Exception{
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        try{
            ServerBootstrap b=new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("http-coder",new HttpServerCodec());
                            //HttpObjectAggregator 它的主要作用是将多个消息转换为单一的FullHttpRequest或FullHttpResponse
                            //这么做主要是因为：Http解码器在每个Http消息中会生成多个消息对象
                            ch.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65536));
                            ch.pipeline().addLast("http-encoder",new HttpResponseEncoder());
                            ch.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
                            ch.pipeline().addLast("websocketserverhandlder",new WebSocketServerHandler2());
                        }
                    });
            Channel ch=b.bind(port).sync().channel();

            System.out.println("Web socket server started at port " + port
                    + '.');
            System.out
                    .println("Open your browser and navigate to http://localhost:"
                            + port + '/');

            ch.closeFuture().sync();

        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception{
        new WebSocketServer().run(NettyConstants.PORT);
    }

}
