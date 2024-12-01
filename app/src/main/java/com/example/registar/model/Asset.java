package com.example.registar.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.registar.util.Constants;

import java.io.Serializable;
import java.time.LocalDate;
@Entity(
        tableName = Constants.TABLE_NAME_ASSET,
        foreignKeys = {
                @ForeignKey(entity = Employee.class, parentColumns = "id", childColumns = "employeeId", onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = Location.class, parentColumns = "id", childColumns = "locationId", onDelete = ForeignKey.SET_NULL)
        })
public class Asset implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private  String description;
    private String imagePath;
    private int barcode;
    private int price;
    private LocalDate creationDate;
    @NonNull
    private Integer employeeId;
    @NonNull
    private Integer locationId;
    
    public Asset(){
        locationId = -1;
        employeeId = -1;
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

    @NonNull
    public Integer getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(@NonNull Integer employeeId) {
        this.employeeId = employeeId;
    }

    @NonNull
    public Integer getLocationId() {
        return locationId;
    }
    public void setLocationId(@NonNull Integer locationId) {
        this.locationId = locationId;
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
