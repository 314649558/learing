package jvm.ch3;

import java.util.ArrayList;
import java.util.List;

/**
 * vm args:-verbose:gc -Xms200M -Xmx200M -XX:+PrintGCDetails -XX:+UseSerialGC
 */
public class FilllHeapDemo {

    public static void fileheap(int num) throws Exception{
        List<OOMOBject> list=new ArrayList<>();
        for (int i=0;i<num;i++){
            //稍作延迟,让曲线变化更加明显
            Thread.sleep(50);
            list.add(new OOMOBject());
        }
        System.gc();
    }

    public static void main(String[] args) throws Exception {
        fileheap(100000);
    }

}
