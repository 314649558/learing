package com.bigdata.sampler.java8action.ch2;

/**
 * Created by Administrator on 2017/11/26 0026.
 */
public class RunnableDemo {

    private static int num1=100;

    public static void main(String[] args) {

        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("num1");
            }
        });

        //jdk8 的实现方式 使用Lambada实现
        Thread t1=new Thread(() -> System.out.println(num1));


    }


}




