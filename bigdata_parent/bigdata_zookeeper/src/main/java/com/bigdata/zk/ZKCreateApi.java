package com.bigdata.zk;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2017/11/22 0022.
 */
public class ZKCreateApi implements Watcher {
    private static CountDownLatch countDownLatch=new CountDownLatch(1);
    private static ZooKeeper zooKeeper=null;
    public static void main(String[] args) throws Exception {
        zooKeeper=new ZooKeeper(Constants.ZK_CONNECT_STR,Constants.ZK_TIMEOUT,new ZKCreateApi());
        //Note: zookeeper 创建连接是异步创建的 因此当你new出对象的时候客户端只是得到了一个创建中的状态，并未真正创建，因此需要等待ZK服务端创建成功后才能执行其他动作
        countDownLatch.await();
        //使用异步的方法创建
        //权限控制模式 world，auth,digest,ip , super
        zooKeeper.addAuthInfo("digest","foo:true".getBytes());
        zooKeeper.create("/zk_book","data_book".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT,new CreateStringCallback(),"创建ZK持久化数据节点");
    }
    @Override
    public void process(WatchedEvent watchedEvent) {
        //如果连接已经创建好
        if(Event.KeeperState.SyncConnected==watchedEvent.getState()){
            System.out.println("Connection Create finish");
            countDownLatch.countDown();
        }
    }
    static class CreateStringCallback implements AsyncCallback.StringCallback{
        @Override
        public void processResult(int rc, String path, Object ctx, String name) {
            /**
             * rc 的值有四种
             *    0 表示成功
             *    4 客户端和服务器连接断开
             *    110 节点已存在
             *    112 会话已过期
             * path   创建节点的路径
             *
             * ctx
             *
             * name 如果是顺序节点会返回一个带有序号的节点
             */
            System.out.println("rc:"+rc);
            System.out.println("path:"+path);
            System.out.println("ctx:"+ctx);
            System.out.println("name:"+name);
        }
    }
}


