package com.example.registar.model;

import java.io.Serializable;
import java.time.LocalDate;

public class FixedAsset implements Serializable {

    private String title, description, picture;
    private int barcode, price;
    private LocalDate creationDate;
    private Employee employee;
    private Location location;
    
    public FixedAsset(){}

    public FixedAsset(String title, String description, String picture, int barcode, int price,
                      LocalDate createDate, Employee employee, Location location) {
        this.title = title;
        this.description = description;
        this.picture = picture;
        this.barcode = barcode;
        this.price = price;
        this.creationDate = createDate;
        this.employee = employee;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getBarcode() {
        return barcode;
    }

    public void setBarcode(int barcode) {
        this.barcode = barcode;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
