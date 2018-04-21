package com.bigdata.flink.java.training;


import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class ChooseEventNumber {

    public static void main(String[] args) throws Exception{
        final StreamExecutionEnvironment env=StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        env.addSource(new LongSource())
                .setParallelism(2)
                .filter(new RichFilterFunction<Integer>() {
                    private Counter filterOutNumber;
                    @Override
                    public void open(Configuration parameters) throws Exception {
                        filterOutNumber=getRuntimeContext()
                                .getMetricGroup()
                                .addGroup("hailong")
                                .counter("filterOutNumber");
                    }
                    @Override
                    public boolean filter(Integer value) throws Exception {
                        if (value % 2 == 0) {
                            filterOutNumber.inc();
                        }
                        return !(value%2==0);
                    }
                }).print();
        env.execute();
    }


}
