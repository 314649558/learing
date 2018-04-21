package com.bigdata.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2017/11/23 0023.
 */
public class ZKNodeExistsApi implements Watcher {

    private static CountDownLatch countDownLatch=new CountDownLatch(1);

    private static ZooKeeper zooKeeper=null;

    public static void main(String[] args) throws Exception{
        zooKeeper=new ZooKeeper(Constants.ZK_CONNECT_STR,Constants.ZK_TIMEOUT,new ZKNodeExistsApi());
        countDownLatch.await();
        Stat stat = zooKeeper.exists(Constants.ZK_BOOK,true);
        if(stat==null) {
            String str = zooKeeper.create(Constants.ZK_BOOK, "zk_book_data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        Stat stat1 = zooKeeper.setData(Constants.ZK_BOOK,"zk_book_data_v2".getBytes(),-1);

        zooKeeper.create(Constants.ZK_BOOK+"/c1","c1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);

        //note: zookeeper 只能从叶子目录一层层向上删除呢
        //      zookeeper 创建目录不能递归创建
        zooKeeper.delete(Constants.ZK_BOOK+"/c1",-1);
        zooKeeper.delete(Constants.ZK_BOOK,-1);

        Thread.sleep(Integer.MAX_VALUE);
    }


    @Override
    public void process(WatchedEvent event) {
        try {
            if(Event.KeeperState.SyncConnected==event.getState()){
                if(EventType.None ==event.getType() && null == event.getPath()) {
                    countDownLatch.countDown();
                    System.out.println("Connection Created");
                }else if(EventType.NodeCreated == event.getType()){
                    System.out.println("Node ["+event.getPath()+" ] Created");
                    zooKeeper.exists(event.getPath(),true);
                }else if (EventType.NodeDeleted == event.getType()){
                    System.out.println("Node ["+event.getPath()+" ] Deleted");
                    zooKeeper.exists(event.getPath(),true);
                }else if (EventType.NodeDataChanged == event.getType()){
                    System.out.println("Node ["+event.getPath()+" ] DataChanged");
                    zooKeeper.exists(event.getPath(),true);
                }
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }
}
