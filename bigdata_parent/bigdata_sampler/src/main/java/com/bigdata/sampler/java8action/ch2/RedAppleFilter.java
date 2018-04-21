package com.bigdata.sampler.java8action.ch2;

/**
 * Created by Administrator on 2017/11/26 0026.
 *  策略模式具体算法实现（在JAVA中这种实现可理解为多态）
 */
public class RedAppleFilter implements ApplePredicate {
    @Override
    public boolean test(Apple apple) {
        return "Red".equalsIgnoreCase(apple.getColor());
    }


}
