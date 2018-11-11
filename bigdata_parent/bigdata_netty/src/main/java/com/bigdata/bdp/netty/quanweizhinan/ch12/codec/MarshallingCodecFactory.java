package com.bigdata.bdp.netty.quanweizhinan.ch12.codec;

import org.jboss.marshalling.*;

import java.io.IOException;

/**
 * Created by Administrator on 2018/11/10.
 */
public class MarshallingCodecFactory {


    /**
     * 创建JBOSS Marshaller
     * @return
     * @throws IOException
     */
    protected static Marshaller buildMarshalling() throws IOException{
        final MarshallerFactory marshallerFactory=Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration=new MarshallingConfiguration();
        configuration.setVersion(5);
        Marshaller marshalling=marshallerFactory.createMarshaller(configuration);
        return marshalling;
    }

    /**
     * 创建JBOSS Unmarshaller
     * @return
     * @throws IOException
     */
    protected static Unmarshaller buildUnMarshalling() throws IOException{
        final MarshallerFactory marshallerFactory=Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        Unmarshaller unmarshaller=marshallerFactory.createUnmarshaller(configuration);
        return unmarshaller;
    }

}
