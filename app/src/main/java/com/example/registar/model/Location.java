package com.example.registar.model;

import java.io.Serializable;

public class Location implements Serializable {

    private String city;

    public Location(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
