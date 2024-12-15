package com.example.registar.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.registar.util.Constants;

import java.io.Serializable;
@Entity(
        tableName = Constants.TABLE_NAME_LIST_ITEM,
        foreignKeys = {
                @ForeignKey(
                        entity = AssetList.class,
                        parentColumns = "id",
                        childColumns = "assetListId",
                        onDelete = ForeignKey.CASCADE // Cascade delete if AssetList is deleted
                ),
                @ForeignKey(
                        entity = Employee.class,
                        parentColumns = "id",
                        childColumns = "oldEmployeeId",
                        onDelete = ForeignKey.SET_NULL // Set to null if Employee is deleted
                ),
                @ForeignKey(
                        entity = Employee.class,
                        parentColumns = "id",
                        childColumns = "newEmployeeId",
                        onDelete = ForeignKey.SET_NULL
                ),
                @ForeignKey(
                        entity = Location.class,
                        parentColumns = "id",
                        childColumns = "oldLocationId",
                        onDelete = ForeignKey.SET_NULL
                ),
                @ForeignKey(
                        entity = Employee.class,
                        parentColumns = "id",
                        childColumns = "newLocationId",
                        onDelete = ForeignKey.SET_NULL
                )
        })
public class ListItem implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private Integer assetListId;
    private Integer oldEmployeeId;
    private Integer newEmployeeId;
    private Integer oldLocationId;
    private Integer newLocationId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getAssetListId() {
        return assetListId;
    }

    public void setAssetListId(Integer assetListId) {
        this.assetListId = assetListId;
    }

    public Integer getNewEmployeeId() {
        return newEmployeeId;
    }

    public void setNewEmployeeId(Integer newEmployeeId) {
        this.newEmployeeId = newEmployeeId;
    }

    public Integer getNewLocationId() {
        return newLocationId;
    }

    public void setNewLocationId(Integer newLocationId) {
        this.newLocationId = newLocationId;
    }

    public Integer getOldEmployeeId() {
        return oldEmployeeId;
    }

    public void setOldEmployeeId(Integer oldEmployeeId) {
        this.oldEmployeeId = oldEmployeeId;
    }

    public Integer getOldLocationId() {
        return oldLocationId;
    }

    public void setOldLocationId(Integer oldLocationId) {
        this.oldLocationId = oldLocationId;
    }
}
