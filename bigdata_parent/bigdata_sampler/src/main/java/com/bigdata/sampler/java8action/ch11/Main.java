package com.bigdata.sampler.java8action.ch11;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Administrator on 2017/12/5 0005.
 */
public class Main {

    private static List<Shop> shops=new ArrayList<>();

    private static Executor executor=null;

    static {
        Random random=new Random();
        for (int i=1;i<=100;i++) {
            shops.add(new Shop("Apple"+i));
        }
        executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread=new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        });
    }



    public List<String> findPrice(){

        long beginTime=System.currentTimeMillis();
        List<String> lst=shops.parallelStream().map(shop->String.format("%s price is %.2f",
                shop.getProduct(),
                shop.getPrice()))
                .collect(Collectors.toList());

        System.out.println("parallelStream use time:"+(System.currentTimeMillis()-beginTime));
        return lst;
    }

    public List<String> findPrice2(){
        long beginTime=System.currentTimeMillis();
        //使用CompletableFuture异步计算每种商品的价格
        List<CompletableFuture<String>> futures=shops.
                parallelStream().
                map(shop->CompletableFuture.
                        supplyAsync(()->String.format("%s price is %.2f",
                                shop.getProduct(),
                                shop.getPrice()),executor)).
                collect(Collectors.toList());
        //等待所有的异步结果计算完成
        List<String> lst=futures.parallelStream().map(CompletableFuture::join).collect(Collectors.toList());
        System.out.println("CompletableFuture use time:"+(System.currentTimeMillis()-beginTime));
        return lst;
    }


    public List<String> findPrice3(String product){
        long beginTime=System.currentTimeMillis();
        List lst=shops.parallelStream()
                .map(shop -> shop.getPrice(product))
                .map(Quote::parse)
                .map(Discount::applyDiscount)
                .collect(Collectors.toList());
        System.out.println("findPrice3 use time:"+(System.currentTimeMillis()-beginTime));
        return lst;
    }



    public List<String> findPrice4(String product){
        long beginTime=System.currentTimeMillis();
        List<CompletableFuture<String>> futures=shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(()->shop.getPrice(product),executor))
                .map(future -> future.thenApply(Quote::parse))
                //thenCompose 将第一个CompletableFuture的结果传递给第二个CompletableFuture
                .map(future -> future.thenCompose(quote -> CompletableFuture.supplyAsync(()->Discount.applyDiscount(quote),executor)))
                .collect(Collectors.toList());

        System.out.println("xxxxxxxxxxxxxxxxx");

        List<String> lst=futures.stream().map(CompletableFuture::join).collect(Collectors.toList());

        System.out.println("findPrice4 use time:"+(System.currentTimeMillis()-beginTime));
        return lst;
    }


    public List<Double> findPrice5(String product){
        long beginTime=System.currentTimeMillis();
        List<CompletableFuture<Double>> futures=shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(()->shop.getPrice(),executor))
                .map(future->future.thenCombine(CompletableFuture.supplyAsync(()->{ //thenCombine 表示两个毫不相干的CompletableFuture 并行计算，计算完成后，可以对结果进行组合
                    try {
                        Thread.sleep(1000);
                    }catch(Exception e){

                    }
                   return new Random().nextDouble();
                }),(price,rate)->price*rate)).collect(Collectors.toList());

        List<Double> lst=futures.stream().map(CompletableFuture::join).collect(Collectors.toList());

        System.out.println("findPrice4 use time:"+(System.currentTimeMillis()-beginTime));
        return lst;
    }


    public Stream<CompletableFuture<String>> findPricesStream(String product) {
        return shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> shop.getPrice(product), executor))
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> future.thenCompose(quote ->
                        CompletableFuture.supplyAsync(
                                () -> Discount.applyDiscount(quote), executor)));
    }


    //thenAccept 部分结果完成后就立即执行
    public void futures(){
        long beginTime=System.currentTimeMillis();
        CompletableFuture[] futures = findPricesStream("myPhone")
                .map(f -> f.thenAccept(s-> System.out.println("(done in "+(System.currentTimeMillis()-beginTime)+" )")))
                .toArray(size -> new CompletableFuture[size]);
        CompletableFuture.allOf().join();
    }




    public static void main(String[] args) {
        Main main=new Main();
       /* main.findPrice();
        main.findPrice2();*/


        main.futures();
    }
}
