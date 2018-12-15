package com.bigdata.kafka.sourceparse.network;

import org.apache.kafka.common.security.authenticator.SaslClientAuthenticator;

/**
 * Created by Administrator on 2018/12/16.
 *
 * 模拟kafka源码中认证过程 主要代码  并没有做实现，主要是借鉴源码思路
 *
 */
public class SaslClientRequestAuthenticatorDemo {

    public enum SaslClientState{
        SEND_HANDSHAKE_REQUEST,
        RECEIVE_HANDSHAKE_RESPONSE,
        INITIAL,
        COMPLETE,
        FAILED
    }


    private SaslClientState saslClientState=SaslClientState.SEND_HANDSHAKE_REQUEST;


    public void setSaslClientState(SaslClientState saslClientState){
        this.saslClientState=saslClientState;
    }



    public void authenticate(){
        switch (saslClientState){
            case SEND_HANDSHAKE_REQUEST:
                send();
                setSaslClientState(SaslClientState.RECEIVE_HANDSHAKE_RESPONSE);
                break;
            case RECEIVE_HANDSHAKE_RESPONSE:
                receiveKafkaResponse();
                setSaslClientState(SaslClientState.INITIAL);
                break;
            case INITIAL:
                boolean flag=sendSaslClientToken();
                if(flag){
                    setSaslClientState(SaslClientState.COMPLETE);
                }else {
                    setSaslClientState(SaslClientState.FAILED);
                }
                break;
            case FAILED:
                throw new IllegalStateException("SASL handshake has already failed");
            case COMPLETE:
                break;
            default:
                throw new IllegalStateException("SASL handshake has already failed");
        }
    }

    private boolean sendSaslClientToken() {
        //握手完成后，发送认证Token
        return true;
    }


    private void receiveKafkaResponse() {
        //TODO 处理服务端的响应
    }


    private void send() {
        //TODO 向服务端发送我握手请求协议
    }


    public static void main(String[] args) {
        new Thread(new Runnable() {
            final SaslClientRequestAuthenticatorDemo saslClientRequestAuthenticatorDemo=new SaslClientRequestAuthenticatorDemo();
            @Override
            public void run() {
                while(true){
                    saslClientRequestAuthenticatorDemo.authenticate();
                }
            }
        }).start();
    }


}
