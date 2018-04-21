package com.bigdata.sampler.java8action.ch11;

import java.util.Random;

/**
 * Created by Administrator on 2017/12/5 0005.
 */
public class Shop {
    private String product;


    public Shop(String product) {
        this.product = product;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public static double getPrice(){
        return calculatePrice();
    }


    public static String getPrice(String product){
        Discount.Code code=Discount.Code.values()[new Random().nextInt(Discount.Code.values().length)];
        return String.format("%s:%.2f:%s",product,calculatePrice(),code);
    }

    private static double calculatePrice(){
        dealy();
        Random random=new Random();
        return random.nextDouble();
    }

    public static void dealy(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
