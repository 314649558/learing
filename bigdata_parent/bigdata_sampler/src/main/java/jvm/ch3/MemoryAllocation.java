package jvm.ch3;

import jvm.JVMConstants;

import java.util.ArrayList;
import java.util.List;


public class MemoryAllocation {

    /**
     *
     *
     * vm args: -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:+UseSerialGC
     * 参数含义:
     *        java堆内存设置为20m 并且不可扩展
     *        -Xmn  设置新生代的Edge区域为10 老年代为20-10
     *        -XX:SurvivorRatio=8 表示 新生代Edge和Survior区域的堆内存分配比为8:1
     *        -XX:+UseSerialGC  使用GC 类型
     *
     * DESC:
     *  观察内存分配情况
     *
     */
    public static void testMemoryAllocation(){
        byte[] allocation1,allocation2,allocation3,allocation4;

        allocation1=new byte[2 * JVMConstants._1MB];
        allocation2=new byte[2 * JVMConstants._1MB];
        allocation3=new byte[2 * JVMConstants._1MB];
        //这里会出现一次minor GC
        allocation4=new byte[4 * JVMConstants._1MB];
    }


    /**
     *
     *
     * vm args: -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:PretenureSizeThreshold=3145728 -XX:+UseSerialGC
     * 参数含义:
     *   -XX:PretenureSizeThreshold 当一个对象分配的内存大于这个数值后直接进入老年代，避免在年轻带大量的复制工作
     *
     * DESC:
     *  测试大对象直接进入老年代
     */
    public static void testPretenureSizeThreshold(){
        byte[] allocation;
        allocation=new byte[4 * JVMConstants._1MB];
    }



    /**
     * vm args: -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:MaxTenuringThreshold=10 -XX:+UseSerialGC
     * 参数含义:
     *   -XX:MaxTenuringThreshold 对象如果在在Surviver区域进行过一次Minor GC  对象的年龄计数器就会+1  默认为15  ，当达到设定值后进入老年代中
     *   -XX:HandlePromotionFailure         是否允许担保失败（jdk1.6以后失效）
     *
     * DESC:
     *    测试长期存货的对象是否会进入老年代，可以改变这个参数的值看效果
     */
    public static void testTenuringThreshold() throws Exception{
        byte[] allocation1,allocation2,allocation3;

        allocation1=new byte[JVMConstants._1MB / 4];
        //什么时候进入老年代取决于MaxTenuringThreshold的设置
        allocation2=new byte[4 * JVMConstants._1MB];
        allocation3=new byte[4 * JVMConstants._1MB];
        allocation3=null;
        allocation3=new byte[4 * JVMConstants._1MB];

        List lst=new ArrayList();
        while (true){
            lst.add(new byte[1 * JVMConstants._1MB]);
            Thread.sleep(1000);
        }





    }




    public static void main(String[] args) throws Exception {
        //testMemoryAllocation ();
        //testPretenureSizeThreshold();
        testTenuringThreshold();
    }
}
