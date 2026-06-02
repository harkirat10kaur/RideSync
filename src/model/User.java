package com.cabbooking.model;

public class User extends Person {
    private String location;

    public User(String name, String location) {
        super(name);
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public void display() {
        System.out.println("User: " + name + ", Location: " + location);
    }
}