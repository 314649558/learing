package com.hailong.jvm.ref;


import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2019/7/19.
 */
public class WeakRef {
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
        WeakReference<User> userWeakRef=new WeakReference<User>(u);  //将对象u设置为软引用
        u=null;  //清除强引用
        System.out.println(userWeakRef.get());
        System.gc();
        System.out.println("After GC:");
        System.out.println(userWeakRef.get());
    }
}
