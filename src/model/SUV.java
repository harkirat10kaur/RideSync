package com.cabbooking.model;

public class SUV extends Vehicle {

    public SUV() {
        super(20);
    }

    @Override
    public double calculateFare(double distance) {
        return distance * rate;
    }

    @Override
    public String getType() {
        return "SUV";
    }
}