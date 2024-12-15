package com.example.registar.model;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;

public class AssetWithRelations implements Serializable {
    @Embedded
    private Asset asset;
    @Relation(
            parentColumn = "employeeId",
            entityColumn = "id"
    )
    private Employee employee;
    @Relation(
            parentColumn = "locationId",
            entityColumn = "id"
    )
    private Location location;



    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
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

    @NonNull
    @Override
    public String toString() {
        return getAsset().getTitle();
    }
}
