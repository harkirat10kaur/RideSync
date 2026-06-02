package com.cabbooking.model;

public class Mini extends Vehicle {

    public Mini() {
        super(10);
    }

    @Override
    public double calculateFare(double distance) {
        return distance * rate;
    }

    @Override
    public String getType() {
        return "Mini";
    }
}