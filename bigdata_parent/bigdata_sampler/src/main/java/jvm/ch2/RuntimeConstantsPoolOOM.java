package jvm.ch2;

/**
 * 运行时常量池
 * String.intern方法是一个native方法 其作用是：如果字符串常量池已经存在则返回对象的引用，否则将String对象字符串添加到常量池中
 * 在JDK1.6中 首次遇到的字符串实例复制到永久代中，返回的是永久代中实例的引用，而StringBuilder创建的对象是在Java堆上的
 * 在jdk1.7+ 中的intern() 不会复制实例，只是在常量池中记录出现的位置
 */
public class RuntimeConstantsPoolOOM {
    public static void main(String[] args) {
        String str1=new StringBuilder ("计算机").append ("软件").toString ();
        //jdk1.6 返回false jdk1.7+ 返回true
        System.out.println (str1.intern ()==str1);

        String str2=new StringBuilder ("ja").append ("va").toString ();
        //jdk1.6,1.7+ 都返回false
        System.out.println (str2.intern ()==str2);





    }
}
