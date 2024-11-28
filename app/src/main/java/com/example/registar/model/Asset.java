package com.example.registar.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Asset implements Serializable {

    private String title, description, imagePath;
    private int id, barcode, price;
    private LocalDate creationDate;
    private Employee employee;
    private Location location;
    
    public Asset(){}

    public Asset(int id, String title, String description, String imagePath, int barcode, int price,
                 LocalDate createDate, Employee employee, Location location) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.barcode = barcode;
        this.price = price;
        this.creationDate = createDate;
        this.employee = employee;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Asset that = (Asset) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id); // Generate hash code based on id
    }

}
