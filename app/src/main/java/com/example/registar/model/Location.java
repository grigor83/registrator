package com.example.registar.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.registar.util.Constants;

import java.io.Serializable;
@Entity(tableName = Constants.TABLE_NAME_LOCATION)
public class Location implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String city;
    private String address;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    @NonNull
    @Override
    public String toString() {
        return getCity();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Location other = (Location) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id); // Generate hash code based on id
    }
}
