package com.bigdata.sampler.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2017/11/23 0023.
 */
public class SamplerLock extends ReentrantLock {

    private final Condition _timeOutCondition=newCondition();

    public Condition getTimeOutCondition() {
        return _timeOutCondition;
    }
}
