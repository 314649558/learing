package com.bigdata.sampler.java8action.ch5;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2017/11/28 0028.
 */
public class MenuData {

    public static final List<Menu> dataMenuLst=new ArrayList<Menu>();

    private static final String[] names={"fish","apple","pig","beer","daodou","lajiao","paigu","youmaicai","lizi"};

    static{
        Random random = new Random();

        for(int i=0;i<1000;i++){

            Menu menu= new Menu(names[random.nextInt(names.length)],
                    random.nextInt(2000),
                    random.nextInt(2)==1?true:false);

            dataMenuLst.add(menu);


        }
        System.out.println("数据准备完成");
    }

}
