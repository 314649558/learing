package com.bigdata.kafka.sourceparse.metrix.yammer.gauge;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.reporting.ConsoleReporter;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by yuanhailong on 2018/12/19.
 */
public class LearnGauge {
    private List<String> stringList = new LinkedList<>();

    Gauge<Integer> gauge = Metrics.newGauge(LearnGauge.class, "list-size-gauge", new Gauge<Integer>() {
        @Override
        public Integer value() {
            return stringList.size();
        }
    });

    public void inputElement(String input){
        stringList.add(input);
    }

    public static void main(String[] args) throws Exception {
        //周期性报告注册的所有指标到控制台上 这里设定为1秒钟
        ConsoleReporter.enable(1, TimeUnit.SECONDS);
        LearnGauge learnGauge=new LearnGauge();

        for(int i=0;i<100000;i++){
            learnGauge.inputElement(String.valueOf(i));
            Thread.sleep(500);
        }

    }


}
