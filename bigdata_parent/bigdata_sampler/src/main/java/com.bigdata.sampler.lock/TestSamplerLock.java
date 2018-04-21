package com.bigdata.sampler.lock;

import java.util.Date;
import java.util.Random;

/**
 * Created by Administrator on 2017/11/23 0023.
 */
public class TestSamplerLock {

    private static SamplerLock _samplerLock=new SamplerLock();

    private static final Integer timeout=5000;

    public static void main(String[] args) {
        try {
            //获取锁
            _samplerLock.lockInterruptibly();



            String msg="";
            Random random=new Random();

            int a=random.nextInt(10);

            int b=random.nextInt(10);


            System.out.println("start:"+System.currentTimeMillis());
            Date deadLine=new Date(System.currentTimeMillis()+timeout);
           while(a!=b) {
                boolean flag=_samplerLock.getTimeOutCondition().awaitUntil(deadLine);
                if(!flag){
                    msg="获取链接超时";
                    break;
                }
               b=random.nextInt(100);
            }

            System.out.println("end:"+System.currentTimeMillis());

            System.out.println("a="+a+"\t b="+b);

            if(msg.equals("")){
               msg="系统成功获取链接";
            }

            System.out.println(msg);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            _samplerLock.unlock();
        }


    }


}
