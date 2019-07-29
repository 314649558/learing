package com.hailong.jvm.cloader;

/**
 * Created by Administrator on 2019/7/29.
 *
 */
public class PrintClassLoaderTree {
    public static void main(String[] args) {
        ClassLoader cl=PrintClassLoaderTree.class.getClassLoader();
        while(cl!=null){
            System.out.println(cl);
            cl=cl.getParent();
        }
    }
}
