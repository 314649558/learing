package com.bigdata.sampler.java8action.ch5;

import java.util.*;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Administrator on 2017/11/28 0028.
 */
public class StreamApi {
    public static void main(String[] args) {

        //如果是蔬菜
        //MenuData.dataMenuLst.stream().filter(Menu::isVegetarian).forEach(System.out::println);


        //获取热量小于1000et
       /* MenuData.dataMenuLst.stream().filter(m->m.getCaloris()<1000).forEach(System.out::println);


        System.out.println(MenuData.dataMenuLst.stream().anyMatch(m->m.getCaloris()<-1));

        System.out.println(MenuData.dataMenuLst.stream().noneMatch(m->m.getCaloris()<-1));

        //返回符合条件的任意一条数据
        Optional<Menu> menu = MenuData.dataMenuLst.stream().filter(m->m.getCaloris()<-1).findAny();

        System.out.println(MenuData.dataMenuLst.stream().map(Menu::getCaloris).reduce(0,Integer::sum));


        MenuData.dataMenuLst.stream().mapToInt(Menu::getCaloris).max().orElse(1);


        System.out.println(IntStream.rangeClosed(1,100).sum());


        System.out.println(Math.sqrt(3)%1);

*/
        //Stream.iterate(new int[]{0,1}, t -> new int[]{t[1],t[0]+t[1]}).map(tuple->tuple[0]).limit(20).forEach(t-> System.out.println(t+"\t"));


        //generate 是有状态的  他会记录当前的状态下一次使用
        //iterate 是无状态的  每次迭代的时候都是创建新的对象
    /*    IntStream.generate(new IntSupplier(){

            private int previous = 0;
            private int current = 1;

            @Override
            public int getAsInt() {
                int oldPrevious = this.previous;
                int nextValue = this.previous + this.current;
                this.previous=this.current;
                this.current=nextValue;
                return oldPrevious;
            }
        }).limit(10).forEach(System.out::println);


        System.out.println(MenuData.dataMenuLst.stream().collect(Collectors.counting()));

        System.out.println(MenuData.dataMenuLst.stream().collect(Collectors.maxBy(Comparator.comparing(Menu::getCaloris))).orElse(new Menu()));

        System.out.println(MenuData.dataMenuLst.stream().collect(Collectors.summingInt(Menu::getCaloris)));
        System.out.println(MenuData.dataMenuLst.stream().collect(Collectors.averagingInt(Menu::getCaloris)));


        System.out.println(MenuData.dataMenuLst.stream().collect(Collectors.summarizingInt(Menu::getCaloris)));

        System.out.println(MenuData.dataMenuLst.stream().map(Menu::getName).collect(Collectors.joining("||")));


        //多级分组   现根据名称分组，然后根据是否蔬菜分组，然后根据是否高热量分组
        MenuData.dataMenuLst.stream().collect(Collectors.groupingBy(Menu::getName,Collectors.groupingBy(Menu::isVegetarian,Collectors.groupingBy(menu->{
            if(menu.getCaloris()>1000){
                return "high";
            }else{
                return "low";
            }
        }))));


        System.out.println(
                MenuData.dataMenuLst.stream().collect(Collectors.groupingBy(Menu::getName,Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparing(Menu::getCaloris)),Optional::get)))
        );*/


        MenuData.dataMenuLst.stream().collect(Collectors.toList());


    }
}
