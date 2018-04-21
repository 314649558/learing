package com.bigdata.sampler.java8action.ch2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import static java.util.Comparator.comparing;
/**
 * Created by Administrator on 2017/11/26 0026.
 */
public class Main {


    private static final List<Apple> appLsts=new ArrayList<>();
    private static final String[] colors={"Red","Green"};
    private static final String[] areas={"China","Jappen","Russia"};
    static {
        Random random = new Random();
        for(int i=0;i<1000;i++){
            appLsts.add(new Apple(colors[random.nextInt(colors.length)],
                                   random.nextInt( 300),
                                    areas[random.nextInt(areas.length)]));

        }
    }


    /**
     * 使用谓词下推
     * 这里实际上相当于定义了一个过滤的模板（可简单理解为模板模式），然后采用定义了接口predicate来过滤，
     * ApplePredicate 可理解为定义了一个策略，具体由子类实现策略算法
     * @param apples
     * @param predicate
     * @return
     */
    public static List<Apple> filterApples1(List<Apple> apples,ApplePredicate predicate){
        List<Apple> retApps=new ArrayList<>();
        for (Apple apple:apples) {
           if(predicate.test(apple)) {
               retApps.add(apple);
               System.out.println(apple);
           }
        }
        return retApps;
    }


    /**
     * 抽象化，是他可以应用在任何对象上
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> List<T> filterApples2(List<T> ts,Predicate<T> predicate) {
        List<T> retApps=new ArrayList<>();
        for (T t:ts){
            predicate.test(t);
            retApps.add(t);
        }
        return retApps;
    }


    /**
     * 排序例子
     */
    public static void sort(){
        appLsts.sort(new Comparator<Apple>() {
            @Override
            public int compare(Apple o1, Apple o2) {

                return o1.getWeight().compareTo(o2.getWeight());
            }
        });
        //使用Lamba进行排序
        appLsts.sort((Apple app1,Apple app2) -> app1.getWeight().compareTo(app2.getWeight()));
    }



    public static void main(String[] args) {
        //使用最常用的方式，子类实现多态行为
        Main.filterApples1(appLsts,new RedAppleFilter());
        // Main.filterApples1(appLsts,new WeightApplerFilter());
        //使用匿名类
        Main.filterApples1(appLsts, new ApplePredicate() {
            @Override
            public boolean test(Apple apple) {
                return apple.getWeight()>200 && apple.getColor().equalsIgnoreCase("red");
            }


        });
        //使用Lambda表达式  JDK8 才可以使用
        Main.filterApples1(appLsts, (Apple apple) -> "red".equalsIgnoreCase(apple.getColor()));


        //将上面的代码写的紧凑一些
        appLsts.sort(comparing((a) -> a.getWeight()));

        //使用方法的引用
        //这段代码含义 首先按Weight排序，然后降序，最后如果有Weight一样的就按MadeArea排序
         appLsts.sort(comparing(Apple::getWeight).reversed().thenComparing(Apple::getMadeArea));


    }

}
