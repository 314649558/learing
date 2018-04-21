package com.bigdata.zk.zkclient;

import com.bigdata.zk.Constants;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;

/**
 * Created by Administrator on 2017/11/23 0023.
 */
public class CreateApi {

    public static void main(String[] args) throws  Exception{
        ZkClient zkClient=new ZkClient(Constants.ZK_CONNECT_STR);

        System.out.println("Session Already established");

        //zkClient.createPersistent("/zk/zk1/zk2",true);

        //zkClient.deleteRecursive("/zk");

        zkClient.subscribeChildChanges(Constants.ZK_BOOK, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> list) throws Exception {
                System.out.println(parentPath + " child changed, currentChilds:"+list);
            }
        });


        /*zkClient.createEphemeralSequential(Constants.ZK_BOOK,"");*/


        if (!zkClient.exists(Constants.ZK_BOOK)) {
            zkClient.createPersistent(Constants.ZK_BOOK);
        }


        zkClient.createEphemeralSequential(Constants.ZK_BOOK+"/c1","");

       /* Thread.sleep(1000);
        System.out.println(zkClient.getChildren(Constants.ZK_BOOK));

        Thread.sleep(1000);
        zkClient.createPersistent(Constants.ZK_BOOK+"/c1");

        Thread.sleep(1000);
        zkClient.delete(Constants.ZK_BOOK+"/c1");

        Thread.sleep(1000);
        zkClient.deleteRecursive(Constants.ZK_BOOK);
*/
        Thread.sleep(Integer.MAX_VALUE);

    }

}
