package com.hailong.jvm.ref;


import java.lang.ref.SoftReference;

/**
 * Created by Administrator on 2019/7/19.
 *
 * -Xms10m -Xmx10m
 */
public class SoftRef {
    public static class User{
        public int id;
        public String name;

        public User(int id,String name){
            this.id=id;
            this.name=name;
        }
        @Override
        public String toString() {
            return "id=["+id+"], name=["+name+"]";
        }
    }
    public static void main(String[] args) {
        User u=new User(1,"hailong");
        SoftReference<User> userSoftRef=new SoftReference<User>(u);  //将对象u设置为软引用
        u=null;  //清除强引用

        System.out.println(userSoftRef.get());
        System.gc();
        System.out.println("After GC:");
        System.out.println(userSoftRef.get());


        byte[] b=new byte[1024*985*6];  //分配heap空间
        System.gc();
        System.out.println("After Allocate Heap: ");
        System.out.println(userSoftRef.get());
    }
}
