package com.bigdata.sampler.java8action.ch10;

import java.util.Optional;





class Person{
    private Optional<Car> car;

    public Optional<Car> getCar() {
        return car;
    }

    public void setCar(Optional<Car> car) {
        this.car = car;
    }
}
class Car{
    private Optional<Insurance> insurance;

    public Optional<Insurance> getInsurance() {
        return insurance;
    }

    public void setInsurance(Optional<Insurance> insurance) {
        this.insurance = insurance;
    }
}

class Insurance{
    private String name;

    public  String getName() {
        return name;
    }


}
