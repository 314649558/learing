package com.bigdata.bdp.netty.quanweizhinan.ch12.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.IOException;

/**
 * Created by Administrator on 2018/11/11.
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    MarshallingDecoder marshallingDecoder;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) throws IOException{
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        marshallingDecoder=new MarshallingDecoder();
    }

}
