package jvm.ch2;


import java.util.ArrayList;
import java.util.List;

/**
 *
 * JAVA 堆溢出
 * JAVA 堆用于储存对象实例，只要不停的创建对象，就会溢出
 * VM Args: -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails
 *          设置堆内存为20M 并不可扩展
 */
public class HeapOOM {
    static class OOMObject{

    }

    public static void main(String[] args) {
        List<OOMObject> list=new ArrayList<> ();
        while (true){
            list.add (new OOMObject ());
        }
    }
}
