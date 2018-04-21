package com.bigdata.sampler.java8action.ch9;

/**
 * Created by Administrator on 2017/12/4 0004.
 */
public interface B {
    default void hello(){
        System.out.println("hello from B");
    };

    public void sayB();
}
