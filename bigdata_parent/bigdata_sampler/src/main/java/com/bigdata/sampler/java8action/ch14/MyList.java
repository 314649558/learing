package com.bigdata.sampler.java8action.ch14;

/**
 * Created by Administrator on 2017/12/6 0006.
 */
public interface MyList<T> {
    T head();

    MyList<T> tail();

    default boolean isEmpty(){
        return true;
    }
}