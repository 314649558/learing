package com.bigdata.sampler.java8action.ch5;

/**
 * Created by Administrator on 2017/11/28 0028.
 */
public class Menu {
    private String name;
    private Integer caloris;
    private boolean isVegetarian;


    public Menu() {
    }
    public Menu(String name, Integer caloris, boolean isVegetarian) {
        this.name = name;
        this.caloris = caloris;
        this.isVegetarian = isVegetarian;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCaloris() {
        return caloris;
    }

    public void setCaloris(Integer caloris) {
        this.caloris = caloris;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        isVegetarian = vegetarian;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "name='" + name + '\'' +
                ", caloris=" + caloris +
                ", isVegetarian=" + isVegetarian +
                '}';
    }
}
