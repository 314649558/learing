package com.bigdata.zk.curator;


import com.bigdata.zk.Constants;
import com.google.common.collect.Maps;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * Created by Administrator on 2017/11/23 0023.
 */
public class CreateZookeeperConnectionApi {
   private static final CuratorFramework zkClient;
   static {
       zkClient=createZKConnection();
   }
    /**
     * 获取客户端链接
     * @return
     */
    private static CuratorFramework createZKConnection(){
        CuratorFramework client=CuratorFrameworkFactory.newClient(Constants.ZK_CONNECT_STR,new ExponentialBackoffRetry(5,1000));
        client.start();
        return client;
    }



    private static void createDateNode(String path,byte[] data){
        try {

            if(zkClient.checkExists().forPath(path)!=null){
                System.out.println("节点已经存在,不能重复创建");
            }else{
                zkClient.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(path,data);
                System.out.println("节点创建成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CreateZookeeperConnectionApi.createDateNode("/curator","curator".getBytes());
    }

}
