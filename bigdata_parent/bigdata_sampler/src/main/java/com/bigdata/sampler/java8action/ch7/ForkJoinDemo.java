package com.bigdata.sampler.java8action.ch7;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * Created by Administrator on 2017/12/1 0001.
 */
public class ForkJoinDemo {

    public static void main(String[] args) {
        Integer sum=new ForkJoinPool().invoke(new Fibonacci(10));
        System.out.println(sum);
    }
}


class Fibonacci extends RecursiveTask<Integer>{

    private Integer n;

    Fibonacci(Integer n){
        this.n=n;
    }

    @Override
    protected Integer compute() {

        if (n<1){
            return n;
        }

        Fibonacci f1 = new Fibonacci(n - 1);
        f1.fork();
        Fibonacci f2 = new Fibonacci(n - 2);
        return f2.compute() + f1.join();
    }
}
