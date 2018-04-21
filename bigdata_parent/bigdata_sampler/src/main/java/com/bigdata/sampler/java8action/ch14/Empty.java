package com.bigdata.sampler.java8action.ch14;

/**
 * Created by Administrator on 2017/12/6 0006.
 */
public class Empty<T> implements MyList<T> {
    @Override
    public T head() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MyList<T> tail() {
        throw new UnsupportedOperationException();
    }
}
