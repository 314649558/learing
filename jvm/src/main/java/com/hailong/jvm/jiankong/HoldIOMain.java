package com.hailong.jvm.jiankong;

import java.io.*;

/**
 * Created by Administrator on 2019/7/23.
 */
public class HoldIOMain {
    public static class HoldIOTask implements Runnable{
        @Override
        public void run() {
            while (true){
                try {
                    FileOutputStream fos=new FileOutputStream(new File("temp"));
                    for(int i=0;i<10000;i++){
                        fos.write(i);  //大量的写操作
                    }
                    fos.close();

                    FileInputStream fis=new FileInputStream(new File("temp"));
                    while (fis.read()!=-1);    //大量的读取数据操作
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        }
    }

    public static class LazyTask implements Runnable{
        @Override
        public void run() {
            try {
                while(true) {
                    Thread.sleep(1000);   //一个空闲线程
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        new Thread(new HoldIOTask()).start(); //开启IO读写繁忙线程
        new Thread(new LazyTask()).start();  //空闲线程
        new Thread(new LazyTask()).start();  //空闲线程
        new Thread(new LazyTask()).start();  //空闲线程
    }
}
