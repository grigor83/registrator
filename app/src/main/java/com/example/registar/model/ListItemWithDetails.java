package com.example.registar.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;

public class ListItemWithDetails implements Serializable {
    @Embedded
    ListItem item;

    @Relation(
            parentColumn = "assetId",
            entityColumn = "id"
    )
    private Asset asset;

    @Relation(
            parentColumn = "oldEmployeeId",
            entityColumn = "id"
    )
    private Employee oldEmployee;

    @Relation(
            parentColumn = "newEmployeeId",
            entityColumn = "id"
    )
    private Employee newEmployee;

    @Relation(
            parentColumn = "oldLocationId",
            entityColumn = "id"
    )
    private Location oldLocation;

    @Relation(
            parentColumn = "newLocationId",
            entityColumn = "id"
    )
    private Location newLocation;

    public ListItem getItem() {
        return item;
    }

    public void setItem(ListItem item) {
        this.item = item;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public Employee getNewEmployee() {
        return newEmployee;
    }

    public void setNewEmployee(Employee newEmployee) {
        this.newEmployee = newEmployee;
    }

    public Location getNewLocation() {
        return newLocation;
    }

    public void setNewLocation(Location newLocation) {
        this.newLocation = newLocation;
    }

    public Employee getOldEmployee() {
        return oldEmployee;
    }

    public void setOldEmployee(Employee oldEmployee) {
        this.oldEmployee = oldEmployee;
    }

    public Location getOldLocation() {
        return oldLocation;
    }

    public void setOldLocation(Location oldLocation) {
        this.oldLocation = oldLocation;
    }
}
