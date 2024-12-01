package com.example.registar.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.registar.model.Location;

import java.util.List;
@Dao
public interface LocationDao {
    @Insert
    long insert(Location location);

    @Update
    void update(Location location);

    @Delete
    void delete(Location location);

    // Note... is varargs, here assets is an array
    @Delete
    void deleteEmployees(Location... locations);

    @Query("SELECT * FROM location")
    List<Location> getAll();

    @Query("SELECT * FROM location WHERE id = :locationId")
    Location getById(int locationId);
}
