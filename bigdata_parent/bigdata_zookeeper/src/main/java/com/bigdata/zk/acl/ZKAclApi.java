package com.bigdata.zk.acl;

import com.bigdata.zk.Constants;
import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2017/11/27 0027.
 */
public class ZKAclApi implements Watcher {

    private static CountDownLatch countDownLatch=new CountDownLatch(1);

    public static void main(String[] args) throws Exception{
        ZooKeeper zk1=new ZooKeeper(Constants.ZK_CONNECT_STR, Constants.ZK_TIMEOUT,new ZKAclApi());
        zk1.addAuthInfo("digest","hailong:true".getBytes());
        zk1.create(Constants.ZK_BOOK,"init".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);

        ZooKeeper zk2=new ZooKeeper(Constants.ZK_CONNECT_STR, Constants.ZK_TIMEOUT,new ZKAclApi());
        zk2.addAuthInfo("digest","hailong:true".getBytes());
        System.out.println(zk2.getData(Constants.ZK_BOOK,false,null));




        /*ZooKeeper zk3=new ZooKeeper(Constants.ZK_CONNECT_STR, Constants.ZK_TIMEOUT,new ZKAclApi());
        zk3.addAuthInfo("digest","hailong:false".getBytes());
        System.out.println(zk3.getData(Constants.ZK_BOOK,false,null));*/

    }


    @Override
    public void process(WatchedEvent event) {
        if(Event.KeeperState.SyncConnected == event.getState()){
            countDownLatch.countDown();
        }
    }
}
