package com.sxd.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ElasticSearchDruidDataSourceFactory;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Administrator on 2018/8/8.
 */
public class ESSQLTest {

    @Test
    public void testESSQL() throws Exception{

        Properties properties = new Properties();
        properties.put("url", "jdbc:elasticsearch://127.0.0.1:9300/testdrools");
        DruidDataSource dds = (DruidDataSource) ElasticSearchDruidDataSourceFactory.createDataSource(properties);
        Connection connection = dds.getConnection();

        String sql="SELECT  " +
                "tradeDate," +
                "deptNbr," +
                "storeNbr," +
                "sum(costAmt) totalCostAmt, " +
                "sum(retailAmt) totalRetailAmt, " +
                "avg(costAmt) avgCostAmt  " +
                "from testdrools where tradeDate>='20180726' group by tradeDate,deptNbr,storeNbr";

        PreparedStatement ps = connection.prepareStatement(sql);
        //PreparedStatement ps = connection.prepareStatement("SELECT  tradeDate ,deptNbr,storeNbr from testdrools where tradeDate>='20180726' group by tradeDate,deptNbr,storeNbr");
        //PreparedStatement ps = connection.prepareStatement("select deptNbr,storeNbr ,sum(retailAmt) retailAmt from testdrools group by deptNbr,storeNbr");
       // PreparedStatement ps = connection.prepareStatement("select gender,sum(balance) total_balance,avg(balance) avg_balance,count(*) num from bank group by gender");
        ResultSet resultSet = ps.executeQuery();


        ResultSetMetaData metaData = resultSet.getMetaData();

        int columnCount=metaData.getColumnCount();

        for(int i=0;i<columnCount;i++){

            System.out.println(metaData.getColumnType(i));

        }

        while (resultSet.next()) {
            /*ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount=metaData.getColumnCount();
            for(int i=0;i<columnCount;i++){
                System.out.println(metaData.getColumnType(i));

            }

            metaData.getColumnType(0);*/
            System.out.println(resultSet.getString("tradeDate"));
            System.out.println(resultSet.getString("deptNbr"));
            System.out.println(resultSet.getString("storeNbr"));
            System.out.println(resultSet.getDouble("totalCostAmt"));
            /*System.out.println(resultSet.getString("totalRetailAmt"));
            System.out.println(resultSet.getString("avgCostAmt"));*/

        }
        ps.close();
        connection.close();
        dds.close();

    }
}
