package jvm.ch3;

/**
 * GC：引用计数器算法
 * 给对象添加一个计数器，如果有一个地方引用他计数器就加1 ；当失效时候就减1 ；任何时刻如果为0，就不在引用（可被回收）
 * 优点：简单
 * 缺点：如果存在循环引用就永远无法释放
 */
public class ReferenceCountingGC
{
    private Object instance=null;

    private static final int _1MB=1024*1024;

    /**
     * 这个属性存在的目的就是为了占用一点点内存，以便可以在GC日志中看清楚是否被回收
     */
    private byte[] bigSize=new byte[2 * _1MB];



    public static void testGC() {
        ReferenceCountingGC objA=new ReferenceCountingGC ();

        ReferenceCountingGC objB=new ReferenceCountingGC ();

        /**
         * 模拟两个对象相互引用的情况下，看GC是否可以回收
         */
        objA.instance=objB;
        objB.instance=objA;

        objA=null;
        objB=null;

        //假设在这里发生GC，objA和objB是否可以被回收
        System.gc ();
    }

    public static void main(String[] args) {
        ReferenceCountingGC.testGC ();
    }
}
