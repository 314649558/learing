package com.bigdata.flink.java.demo;

import com.bigdata.flink.scala.Constants;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.jdbc.JDBCAppendTableSink;
import org.apache.flink.api.java.io.jdbc.JDBCInputFormat;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.table.api.BatchTableEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.Types;
import org.apache.flink.types.Row;

public class FromMysqlToMysqlDemo {

    public static void main(String[] args) throws Exception{
        ExecutionEnvironment env=ExecutionEnvironment.getExecutionEnvironment();

        BatchTableEnvironment tabEnv= BatchTableEnvironment.getTableEnvironment(env);

        //Table table = mutilDataSet (env, tabEnv);

        Table table = singleDataSet (env, tabEnv);

        JDBCAppendTableSink jdbcSink=JDBCAppendTableSink.builder()
                .setDrivername(Constants.MYSQL_DRIVER_NAME())
                .setDBUrl(Constants.MYSQL_URL())
                .setUsername(Constants.MYSQL_USERNAME())
                .setPassword(Constants.MYSQL_PASSWD())
                .setQuery("insert into test (login_name,role_name) values(?,?)").setParameterTypes(Types.STRING(),Types.STRING()).build();

        table.writeToSink(jdbcSink);

        env.execute();
    }

    /**
     * 多个DataSet 联合查询方法
     * @param env
     * @param tabEnv
     * @return
     */
    private static Table mutilDataSet(ExecutionEnvironment env, BatchTableEnvironment tabEnv) {

        //定义数据类型
        TypeInformation types[]={Types.STRING(),Types.STRING()};

        //定义数据字段名称
        String filedNames[]={"id","name"};

        //行信息
        RowTypeInfo rowTypeInfo=new RowTypeInfo(types,filedNames);

        DataSet<Row> userDS = env.createInput (JDBCInputFormat
                .buildJDBCInputFormat ()
                .setDrivername (Constants.MYSQL_DRIVER_NAME ())
                .setDBUrl (Constants.MYSQL_URL ())
                .setUsername (Constants.MYSQL_USERNAME ())
                .setPassword (Constants.MYSQL_PASSWD ())
                .setQuery ("select cast(id as char),login_name from sys_user")
                .setRowTypeInfo (rowTypeInfo)
                .finish ());

        DataSet<Row> roleDS = env.createInput (JDBCInputFormat
                .buildJDBCInputFormat ()
                .setDrivername (Constants.MYSQL_DRIVER_NAME ())
                .setDBUrl (Constants.MYSQL_URL ())
                .setUsername (Constants.MYSQL_USERNAME ())
                .setPassword (Constants.MYSQL_PASSWD ())
                .setQuery ("select cast(user_id as char),role_name from sys_role")
                .setRowTypeInfo (rowTypeInfo)
                .finish ());

        tabEnv.registerDataSetInternal ("user", userDS);

        tabEnv.registerDataSetInternal ("role", roleDS);

        return tabEnv.sqlQuery ("select u.name,r.name from `user` as u inner join `role` as r on(u.id=r.id)");
    }


    private static Table singleDataSet(ExecutionEnvironment env, BatchTableEnvironment tabEnv){


        //定义数据类型
        TypeInformation types[]={Types.STRING(),Types.STRING()};

        //定义数据字段名称
        String filedNames[]={"login_name","role_name"};

        //行信息
        RowTypeInfo rowTypeInfo=new RowTypeInfo(types,filedNames);


        DataSet<Row> ds = env.createInput (JDBCInputFormat
                .buildJDBCInputFormat ()
                .setDrivername (Constants.MYSQL_DRIVER_NAME ())
                .setDBUrl (Constants.MYSQL_URL ())
                .setUsername (Constants.MYSQL_USERNAME ())
                .setPassword (Constants.MYSQL_PASSWD ())
                .setQuery ("select u.login_name,r.role_name from sys_user u inner join sys_role r on (u.id=r.user_id)")
                .setRowTypeInfo (rowTypeInfo)
                .finish ());
        tabEnv.registerDataSetInternal ("ds", ds);
        return tabEnv.sqlQuery ("select login_name,role_name from `ds`");
    }

}
