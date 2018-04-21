package com.bigdata.sampler.java8action.ch14;

/**
 * Created by Administrator on 2017/12/6 0006.
 */
public class Main {
    public static void main(String[] args) {

        MyList<Integer> l=new MyLinkedList<Integer>(5,new MyLinkedList<Integer>(10,new Empty<Integer>()));

    }
}