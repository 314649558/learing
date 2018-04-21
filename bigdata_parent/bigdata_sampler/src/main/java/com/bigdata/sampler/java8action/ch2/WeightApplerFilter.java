package com.bigdata.sampler.java8action.ch2;

/**
 * Created by Administrator on 2017/11/26 0026.
 */
public class WeightApplerFilter implements ApplePredicate {
    @Override
    public boolean test(Apple apple) {
        return apple.getWeight() > 200;
    }



}
