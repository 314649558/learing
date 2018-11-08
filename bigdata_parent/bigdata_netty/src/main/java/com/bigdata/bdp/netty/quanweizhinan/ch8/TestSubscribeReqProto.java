package com.bigdata.bdp.netty.quanweizhinan.ch8;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Created by Administrator on 2018/11/8.
 */
public class TestSubscribeReqProto {

    private static byte[] encode(SubscribeReqProto.SubscribeReq req){
        return req.toByteArray();
    }

    private static SubscribeReqProto.SubscribeReq decode(byte[] body) throws InvalidProtocolBufferException{
        return SubscribeReqProto.SubscribeReq.parseFrom(body);
    }

    private static SubscribeReqProto.SubscribeReq createSubscribeReq(){
        SubscribeReqProto.SubscribeReq.Builder builder=SubscribeReqProto.SubscribeReq
                .newBuilder();

        builder.setSubReqID(1);
        builder.setUserName("hailong");
        builder.setProductName("Netty Book");
        builder.addAddress("Guangdong Shenzhen");
        builder.addAddress("Hunan Changsha");
        builder.addAddress("Hainan Sanya");
        builder.addAddress("Guangxi Nanning");
        return builder.build();
    }

    public static void main(String[] args) throws Exception{
        SubscribeReqProto.SubscribeReq req=createSubscribeReq();
        System.out.println("before encode :\n" + req.toString());

        SubscribeReqProto.SubscribeReq req2=decode(encode(req));
        System.out.println("After decode :\n"+ req.toString());

        System.out.println("Assert equal : --->" + req2.equals(req));
    }

}
