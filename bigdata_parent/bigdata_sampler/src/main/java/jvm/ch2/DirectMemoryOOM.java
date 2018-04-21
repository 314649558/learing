package jvm.ch2;


import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * 直接内存溢出（DirectMemory）
 * VM args: -Xmx20M -XX:MaxDirectMemorySize=10M
 *
 */
public class DirectMemoryOOM {
    public static final int _1MB=1024*1024;

    public static void main(String[] args) throws Exception {
        Field unsafeField= Unsafe.class.getDeclaredFields ()[0];
        unsafeField.setAccessible (true);
        Unsafe unsafe=(Unsafe) unsafeField.get(null);
        while (true){
            unsafe.allocateMemory (_1MB);  //分配物理内存
        }
    }
}
