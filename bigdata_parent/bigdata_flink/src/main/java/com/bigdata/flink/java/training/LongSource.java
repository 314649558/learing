package com.bigdata.flink.java.training;

import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.util.Random;

public class LongSource extends RichParallelSourceFunction<Integer> {

    private volatile boolean isRunning=true;

    private Random ran=new Random(47);

    @Override
    public void run(SourceContext<Integer> ctx) throws Exception {
        while(isRunning){
            for(int i=0;i<10;i++){
                ctx.collect(ran.nextInt()*i);
            }
            Thread.sleep(10);
        }
    }
    @Override
    public void cancel() {
        isRunning=false;
    }
}