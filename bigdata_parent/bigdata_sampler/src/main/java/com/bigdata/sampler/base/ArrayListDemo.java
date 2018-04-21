package com.bigdata.sampler.base;

import org.apache.shiro.web.servlet.ShiroHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class ArrayListDemo {
    public static void main(String[] args) {




       /* HashMap<String,String> hashMap=new HashMap<String,String>(2,0.75f);

        hashMap.put("a","a");
        hashMap.put("b","a");
        hashMap.put("c","a");
        hashMap.put("d","a");*/


        System.out.println(("j" + "v" + "m") == "jvm");
    }


    public static void insertOrder(){
        LinkedHashMap<String,String> map=new LinkedHashMap();
        map.put("apple", "苹果");
        map.put("watermelon", "西瓜");
        map.put("banana", "香蕉");
        map.put("peach", "桃子");


        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }


    public static void visitOrder(){
        LinkedHashMap<String,String> map=new LinkedHashMap(16,0.75f,true);
        map.put("apple", "苹果");
        map.put("watermelon", "西瓜");
        map.put("banana", "香蕉");
        map.put("peach", "桃子");


        map.get("banana");
        map.get("watermelon");




        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }







}
