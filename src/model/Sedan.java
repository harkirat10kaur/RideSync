package com.cabbooking.model;

public class Sedan extends Vehicle {

    public Sedan() {
        super(15);
    }

    @Override
    public double calculateFare(double distance) {
        return distance * rate;
    }

    @Override
    public String getType() {
        return "Sedan";
    }
}