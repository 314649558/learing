package com.bigdata.sampler.java8action.ch9;

/**
 * Created by Administrator on 2017/12/4 0004.
 */
public interface Animal {

    public void say();


     default void xingzou(){
        System.out.println(" JDK8 提供的默认方法,这样如果接口需要添加方法 ， 子类如果不显示的实现，就会默认实现了此方法");
    }

}
