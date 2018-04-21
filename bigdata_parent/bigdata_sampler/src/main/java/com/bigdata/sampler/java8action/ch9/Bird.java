package com.bigdata.sampler.java8action.ch9;

/**
 * Created by Administrator on 2017/12/4 0004.
 */
public class Bird implements Animal {
    @Override
    public void say() {
        System.out.println("bird say hello");
    }


    @Override
    public void xingzou() {
        System.out.println("覆盖默认方法");
    }




}

