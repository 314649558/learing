package com.sxd.citic.drools.utils.elasticsearch;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.ElasticSearchDruidDataSourceFactory;

import java.sql.PreparedStatement;
import java.util.Properties;

/**
 * Created by Administrator on 2018/8/14.
 */
public class ESDruidJavaUtils {

    public static DruidPooledConnection getDruidDataSource(String url) {
        try {
            Properties properties = new Properties();
            properties.put("url", url);
            DruidDataSource dds = (DruidDataSource) ElasticSearchDruidDataSourceFactory.createDataSource(properties);
            return dds.getConnection();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static PreparedStatement getPreparedStatement(DruidPooledConnection conn,String sql){
        try {
            return conn.prepareStatement(sql);
        }catch (Exception ex){
            return null;
        }
    }

}
