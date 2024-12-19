package com.example.registar.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.registar.util.Constants;

import java.io.Serializable;
import java.time.LocalDate;
@Entity(
        tableName = Constants.TABLE_NAME_ASSET,
        indices = @Index(value = "barcode", unique = true),
        foreignKeys = {
                @ForeignKey(entity = Employee.class, parentColumns = "id", childColumns = "employeeId", onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = Location.class, parentColumns = "id", childColumns = "locationId", onDelete = ForeignKey.SET_NULL)
        })
public class Asset implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private String imagePath;

    private long barcode;
    private int price;
    private LocalDate creationDate;
    private Integer employeeId;
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

    public long getBarcode() {
        return barcode;
    }
    public void setBarcode(long barcode) {
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

    public Integer getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getLocationId() {
        return locationId;
    }
    public void setLocationId(Integer locationId) {
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

    @NonNull
    @Override
    public String toString() {
        return getTitle();
    }
}
