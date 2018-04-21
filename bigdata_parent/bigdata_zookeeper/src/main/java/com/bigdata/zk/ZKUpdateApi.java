package com.bigdata.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2017/11/24 0024.
 */
public class ZKUpdateApi implements Watcher {

    private static  ZooKeeper zookeeper=null;

    private static  CountDownLatch countDownLatch = new CountDownLatch(1);


    public static void main(String[] args) throws Exception {

        zookeeper=new ZooKeeper(Constants.ZK_CONNECT_STR,Constants.ZK_TIMEOUT,new ZKUpdateApi());

        countDownLatch.await();

        zookeeper.create(Constants.ZK_BOOK,"init data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        //Note:在Zookeeper中所有的版本号都是从0开始的  -1  仅仅是一个标志，表示用最新的版本去更新
        Stat stat1=zookeeper.setData(Constants.ZK_BOOK,"change data 1".getBytes(),-1);
        System.out.println("-----------1------------");
        System.out.println(stat1.toString());


        Stat stat2=zookeeper.setData(Constants.ZK_BOOK,"change data 2".getBytes(),stat1.getVersion());
        System.out.println("-----------2------------");
        System.out.println(stat1.toString());

        //这次更新不成功，因为版本号对应不上，因此可以利用zk的这种特性实现分布式乐观锁检查
        Stat stat3=zookeeper.setData(Constants.ZK_BOOK,"change data 3".getBytes(),stat1.getVersion());
        System.out.println("-----------3------------");
        System.out.println(stat1.toString());
    }


    @Override
    public void process(WatchedEvent event) {
        if(Event.KeeperState.SyncConnected == event.getState()){
            countDownLatch.countDown();
        }
    }
}
