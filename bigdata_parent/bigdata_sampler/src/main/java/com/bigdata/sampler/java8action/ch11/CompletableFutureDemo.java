package com.bigdata.sampler.java8action.ch11;

import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by Administrator on 2017/12/5 0005.
 */
public class CompletableFutureDemo {

    public static void main(String[] args) throws Exception{


        Future<Double> f=getPriceAnsy();

        System.out.println("xxxxxxxxxxxxx");

        System.out.println(f.get());

        System.out.println("ff");


    }


    public static double getPrice(){
        return calculatePrice();
    }





    public static Future<Double> getPriceAnsy(){
        /*CompletableFuture<Double> futurePrice=new CompletableFuture<>();
        new Thread(()->{
            try {

                double price = calculatePrice();
                if(price<0.5){
                    throw new Exception("price < 0.5");
                }
                futurePrice.complete(price);
            }catch (Exception e){
                futurePrice.completeExceptionally(e);
            }
        }).start();
        return futurePrice;*/

        return CompletableFuture.supplyAsync(()->calculatePrice());
    }

    public static Future<Double> getPriceAnsy2(){
        ExecutorService executor = Executors.newCachedThreadPool();
        return executor.submit(()->{
            return new Random().nextDouble();
        });
    }


    public static double calculatePrice(){
        dealy();
        Random random=new Random();
        return random.nextDouble();
    }


    public static void dealy(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
