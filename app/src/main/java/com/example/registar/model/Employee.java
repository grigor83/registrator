package com.example.registar.model;

import java.io.Serializable;

public class Employee implements Serializable {

    private String name, lastName, position;

    public Employee(String name, String lastName, String position) {
        this.name = name;
        this.lastName = lastName;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName(){
        return name + " " + lastName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
