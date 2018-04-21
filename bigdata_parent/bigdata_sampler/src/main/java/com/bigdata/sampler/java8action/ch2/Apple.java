package com.bigdata.sampler.java8action.ch2;

/**
 * Created by Administrator on 2017/11/26 0026.
 */
public class Apple {

    private String color;
    private Integer weight;
    private String madeArea;



    public Apple(String color, Integer weight, String madeArea) {
        this.color = color;
        this.weight = weight;
        this.madeArea = madeArea;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getMadeArea() {
        return madeArea;
    }

    public void setMadeArea(String madeArea) {
        this.madeArea = madeArea;
    }

    @Override
    public String toString() {
        return "Apple{" +
                "color='" + color + '\'' +
                ", weight=" + weight +
                ", madeArea='" + madeArea + '\'' +
                '}';
    }
}
