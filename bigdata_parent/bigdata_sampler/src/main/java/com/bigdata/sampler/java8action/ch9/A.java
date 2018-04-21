package com.bigdata.sampler.java8action.ch9;

/**
 * Created by Administrator on 2017/12/4 0004.
 *
 * 默认方法签名相同时的继承规则：
 *  1 首先，类或父类中显式声明的方法，其优先级高于所有的默认方法
 *  2 如果用第一条无法判断，方法签名又没有区别，那么选择提供最具体实现的默认方法的 接口
 *  3 最后，如果冲突依旧无法解决，你就只能在你的类中覆盖该默认方法，显式地指定在你的类中使用哪一个接口中的方法。
 */
public interface A {

    default void hello(){
        System.out.println("hello from A");
    };

    public void sayA();

}
