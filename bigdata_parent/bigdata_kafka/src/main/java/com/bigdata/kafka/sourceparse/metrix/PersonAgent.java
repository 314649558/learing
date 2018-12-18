package com.bigdata.kafka.sourceparse.metrix;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * Created by Administrator on 2018/12/16.
 */
public class PersonAgent {
    public static void main(String[] args) throws Exception{
        MBeanServer server= ManagementFactory.getPlatformMBeanServer();
        ObjectName personName=new ObjectName("jmxBean:name=hailong");
        //注册MBean
        server.registerMBean(new Person("hailong",30),personName);
        Thread.sleep(60*60*1000);
    }
}
