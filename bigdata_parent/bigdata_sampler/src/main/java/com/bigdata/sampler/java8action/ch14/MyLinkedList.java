package com.bigdata.sampler.java8action.ch14;

/**
 * Created by Administrator on 2017/12/6 0006.
 */
public class MyLinkedList<T> implements MyList<T> {

    private final T head;
    private final MyList<T> tail;
    public MyLinkedList(T head, MyList<T> tail) {
        this.head = head;
        this.tail = tail;
    }


    @Override
    public T head() {
        return null;
    }

    @Override
    public MyList<T> tail() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
