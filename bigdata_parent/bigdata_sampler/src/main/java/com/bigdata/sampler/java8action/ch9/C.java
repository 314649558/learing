package com.bigdata.sampler.java8action.ch9;

/**
 * Created by Administrator on 2017/12/4 0004.
 */
public class C implements A,B {
    @Override
    public void sayB() {

    }

    @Override
    public void hello() {
        B.super.hello();
    }

    @Override
    public void sayA() {

    }
}
