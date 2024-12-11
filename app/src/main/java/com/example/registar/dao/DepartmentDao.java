package com.example.registar.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.registar.model.Department;

import java.util.List;
@Dao
public interface DepartmentDao {
    @Insert
    long insert(Department department);

    @Update
    void update(Department department);

    @Delete
    void delete(Department department);

    @Query("SELECT * FROM department")
    List<Department> getAll();

}
