package com.cabbooking.model;

import com.cabbooking.service.FareCalculator;

public abstract class Vehicle implements FareCalculator {

    protected double rate;

    public Vehicle(double rate) {
        this.rate = rate;
    }

    // ✅ ADD THIS
    public abstract String getType();
}