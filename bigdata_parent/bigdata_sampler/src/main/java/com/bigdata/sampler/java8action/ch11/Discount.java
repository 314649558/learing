package com.bigdata.sampler.java8action.ch11;

/**
 * Created by Administrator on 2017/12/5 0005.
 */
public class Discount {
    public enum Code{
        NONE(0), SILVER(5), GOLD(10), PLATINUM(15), DIAMOND(20);
        private final int percentage;
        Code(int percentage){
            this.percentage=percentage;
        }
    }




    public static String applyDiscount(Quote quote){
        return quote.getShopName() + " price is " +
                Discount.apply(quote.getPrice(),
                        quote.getDiscountCode());
    }

    private static double apply(double price, Code code) {
        Shop.dealy();
        return price * (100 - code.percentage) / 100;
    }
}
