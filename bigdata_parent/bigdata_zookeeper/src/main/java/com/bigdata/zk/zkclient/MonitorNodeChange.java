package com.bigdata.zk.zkclient;

import com.bigdata.zk.Constants;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;

/**
 * Created by Administrator on 2017/11/23 0023.
 */
public class MonitorNodeChange {

    public static void main(String[] args) throws Exception {

        ZkClient zkClient=new ZkClient(Constants.ZK_CONNECT_STR);

        System.out.println("Session Already established");


        zkClient.subscribeChildChanges(Constants.ZK_BOOK, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> list) throws Exception {
                System.out.println(parentPath + " child changed, currentChilds:"+list);
            }
        });

        Thread.sleep(Integer.MAX_VALUE);

    }
}
