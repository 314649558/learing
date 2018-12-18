package com.bigdata.kafka.sourceparse.metrix;

/**
 * Created by Administrator on 2018/12/16.
 */
public class Person implements PersonMBean {

    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getAge() {
        return 0;
    }

    @Override
    public String sayHello(String hello) {
        System.out.println(hello);
        return this.name+":"+hello;
    }
}
