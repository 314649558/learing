package com.bigdata.sampler.java8action.stream;

/**
 * Created by Administrator on 2017/11/25 0025.
 */

import java.util.*;

import static java.util.stream.Collectors.groupingBy;


/**
 * 验证JDK8 Stream Api 对比数据筛选性能
 */
public class StreamApi {

    public static final  List<Transaction> transactions=new ArrayList<>();

    public static final String[] currencyArr={"RMB","OU","MY","RY","FB","YND","LB","D"};



    /**
     * 准备数据
     */
    static {
        Random random=new Random();

        for(int i=0;i<10000000;i++){
            Transaction transaction=new Transaction();
            transaction.setPrice(random.nextInt(2000));
            transaction.setCurrency(currencyArr[random.nextInt(currencyArr.length)]);
            transactions.add(transaction);
        }
    }


    public static void main(String[] args) {
        jdk8();

        trasadtionApi();

    }
    public static void jdk8(){
        long startTime=System.currentTimeMillis();
        Map<String, List<Transaction>> transactionsByCurrencies=
                transactions.parallelStream().filter((Transaction t) -> t.getPrice()>1000)
                        .collect(groupingBy(Transaction::getCurrency));
        System.out.println("--------------stream 使用时间----------------");
        System.out.println(System.currentTimeMillis()-startTime);
    }

    public static void trasadtionApi(){
        Map<String, List<Transaction>> transactionsByCurrencies2=new HashMap<>();
        long startTime=System.currentTimeMillis();
        for (Transaction transaction:transactions) {
            if(transaction.getPrice()>=1000){
                String currency=transaction.getCurrency();
                List<Transaction> transactionsForCurrency =  transactionsByCurrencies2.get(currency);
                if(transactionsForCurrency==null){
                    transactionsForCurrency=new ArrayList<>();
                    transactionsByCurrencies2.put(currency,transactionsForCurrency);
                }
                transactionsForCurrency.add(transaction);
            }
        }

        System.out.println("--------------传统API 使用时间----------------");
        System.out.println(System.currentTimeMillis()-startTime);
    }

}

class Transaction {
   private Integer price;
   private String currency;

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
