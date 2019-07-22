package com.hailong.jvm.ref;


import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

/**
 * Created by Administrator on 2019/7/19.
 *
 * -Xms10m -Xmx10m
 */
public class SoftRefQ {
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

    static ReferenceQueue<User> softQueue=null;


    public static class CheckRefQueue extends Thread{
        @Override
        public void run() {
            while (true){
                if(softQueue!=null){
                    UserSoftReference obj=null;

                    try {
                        obj=(UserSoftReference)softQueue.remove();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(obj!=null){
                        System.out.println("user id "+obj.uid+" is delete");
                    }

                }
            }
        }
    }


    public static class UserSoftReference extends SoftReference<User>{
        int uid;
        public UserSoftReference(User refrent,ReferenceQueue<? super User> q){
            super(refrent,q);
            uid=refrent.id;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t=new CheckRefQueue();
        t.setDaemon(true);
        t.start();

        User u=new User(1,"hailong");
        softQueue=new ReferenceQueue<User>();

        UserSoftReference userSoftReference=new UserSoftReference(u,softQueue);
        u=null;
        System.out.println(userSoftReference.get());
        System.gc();

        System.out.println("After GC:");
        System.out.println(userSoftReference.get());

        System.out.println("try to create byte array and GC");
        byte[] b=new byte[1024*925*7];  //分配heap空间
        System.gc();
        System.out.println(userSoftReference.get());
        Thread.sleep(1000);


    }
}
