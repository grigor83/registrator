package com.example.registar.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.registar.util.Constants;

import java.io.Serializable;
@Entity(tableName = Constants.TABLE_NAME_DEPARTMENT)
public class Department implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @NonNull
    private String name;

    public Department() {
        name = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Department other = (Department) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id); // Generate hash code based on id
    }
}
