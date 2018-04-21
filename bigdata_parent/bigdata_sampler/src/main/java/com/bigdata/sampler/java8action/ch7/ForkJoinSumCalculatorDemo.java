package com.bigdata.sampler.java8action.ch7;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

/**
 * Created by Administrator on 2017/12/1 0001.
 */
public class ForkJoinSumCalculatorDemo {




    public static long forLoop(long n){
        long sum=0;
        for(int i=0;i<=n;i++){
            sum+=i;
        }
        return sum;
    }


    public static long forkJoinSum(long n){
        long[] numbers= LongStream.rangeClosed(0,n).toArray();
        ForkJoinTask<Long> task=new ForkJoinSumCalculator(numbers);
        return new ForkJoinPool().invoke(task);  //note:ForkJoinPool 在实际的应用中 只应该被初始化一次
    }


    public static long streamApi(long n){
        return LongStream.rangeClosed(0,n).parallel().sum();
    }

    public static void main(String[] args) {
        long beginTime=System.currentTimeMillis();
        System.out.println(forkJoinSum(100000000L));
        System.out.println("--use total time:"+(System.currentTimeMillis()-beginTime)+"毫秒");


        beginTime=System.currentTimeMillis();
        System.out.println(forLoop(100000000L));
        System.out.println("--use total time:"+(System.currentTimeMillis()-beginTime)+"毫秒");


        beginTime=System.currentTimeMillis();
        System.out.println(streamApi(100000000L));
        System.out.println("--use total time:"+(System.currentTimeMillis()-beginTime)+"毫秒");
    }
}


class ForkJoinSumCalculator extends RecursiveTask<Long>{

    private long[] numbers;
    private int start;
    private int end;

    private int TH=100000;

    ForkJoinSumCalculator(long[] numbers){
        this(numbers,0,numbers.length);
    }

    ForkJoinSumCalculator(long[] numbers , int start, int end){
        this.numbers=numbers;
        this.start=start;
        this.end=end;
    }

    @Override
    protected Long compute() {

        int length=end-start;

        //如果小于指定吞吐量就顺序执行
        if (length<TH){
            return computeSequentially();
        }


        ForkJoinSumCalculator leftTask=new ForkJoinSumCalculator(numbers,start,start+length/2);
        leftTask.fork();// 利用ForkJoinPool 异步执行新创建的子任务

        ForkJoinSumCalculator rightTask=new ForkJoinSumCalculator(numbers,start+length/2,end);
        Long rightResult=rightTask.compute(); //同步执行第二个子任务

        Long leftResult=leftTask.join(); //读取第一个任务的结果，如果没有完成则阻塞等待
        return rightResult+leftResult;
    }


    private long computeSequentially() {
        long sum=0;
        for(int i=start;i<end;i++){
            sum+=numbers[i];
        }
        return sum;
    }
}

