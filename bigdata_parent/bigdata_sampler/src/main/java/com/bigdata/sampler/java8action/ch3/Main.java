package com.bigdata.sampler.java8action.ch3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

/**
 * Created by Administrator on 2017/11/26 0026.
 */
public class Main {

    private final static Integer index=100000000;

    public static void main(String[] args) {
        test1();
        //test2();
        test3();
    }


    public static void test1(){

        long startTime=System.currentTimeMillis();

        int sum=0;
        for(int i=0;i<index;i++){
            sum+=i;
        }
        System.out.println("time1:"+(System.currentTimeMillis()-startTime));

    }


    public static void test2(){

        long startTime=System.currentTimeMillis();


        Stream.iterate(0L,i->i+1).parallel().limit(index).parallel().reduce(0L,Long::sum);


        System.out.println("time2:"+(System.currentTimeMillis()-startTime));

    }


    public static void test3(){

        long startTime=System.currentTimeMillis();


        LongStream.range(0,index).parallel().reduce(0L,Long::sum);


        System.out.println("time3:"+(System.currentTimeMillis()-startTime));

    }






}
