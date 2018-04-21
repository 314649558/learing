package com.bigdata.sampler.java8action.ch3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Created by Administrator on 2017/11/26 0026.
 */
public class FunctionDemo {

    private static final List<String> lst=new ArrayList<>();

    static {
        lst.add("Function");
        lst.add("Predicate");
        lst.add("Consumer");
    }


    /**
     * Function 函数式接口 表示 接收一个T类型的数据，返回一个R 类型
     * @param list
     * @param function
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T,R> List<R> map(List<T> list, Function<T,R> function){
        List<R> retLst=new ArrayList<>();
        for (T t:list){
            retLst.add(function.apply(t));
        }
        return retLst;
    }


    public static void main(String[] args) {
       /* FunctionDemo.map(lst,(String str) -> str.length());

        System.out.println("-----------------------");

        FunctionDemo.map(Arrays.asList(1,2,3,4,5),(Integer i)-> {return i+":str";});
*/
        Function<Integer,Integer> f1=x -> x+1;
        Function<Integer,Integer> f2=x -> x*1;

        System.out.println(f1.andThen(f2).apply(1));




    }
}
