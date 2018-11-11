package com.bigdata.bdp.netty.quanweizhinan.ch10;

import com.bigdata.bdp.netty.quanweizhinan.NettyConstants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Created by Administrator on 2018/11/10.
 */
public class HttpFileServer {

    private static final String DEFAULT_URL="/bigdata_netty/src/main/java/com/bigdata/bdp/netty/quanweizhinan";

    public void run(final int port,final String url) throws Exception{
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        try{
            ServerBootstrap b=new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,NettyConstants.BACKLOG_SIZE)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //http request 请求解码器
                            ch.pipeline().addLast("http-decoder",new HttpRequestDecoder());
                            //HttpObjectAggregator 它的主要作用是将多个消息转换为单一的FullHttpRequest或FullHttpResponse
                            //这么做主要是因为：Http解码器在每个Http消息中会生成多个消息对象
                            ch.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65536));
                            ch.pipeline().addLast("http-encoder",new HttpResponseEncoder());
                            ch.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
                            ch.pipeline().addLast("fileserver-Handler",new HttpFileServerHandler(url));
                        }
                    });
            ChannelFuture f=b.bind(NettyConstants.LOCAL_HOST,port).sync();
            System.out.println("HTTP 文件目录服务器启动，网址是: http://"+NettyConstants.LOCAL_HOST+":"+port+url);
            f.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception{
        String url=DEFAULT_URL;
        if(args.length==1){
            url=args[0];
        }
        new HttpFileServer().run(NettyConstants.PORT,url);
    }
}
