package com.bigdata.sampler.java8action.ch2;

/**
 * Created by Administrator on 2017/11/26 0026
 * 定义好一个接口 让子类来实现具体的方法，其实就是策略模式
 */
@FunctionalInterface
public interface ApplePredicate {
    abstract boolean test(Apple apple);
}
