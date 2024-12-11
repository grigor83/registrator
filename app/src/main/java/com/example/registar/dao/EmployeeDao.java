package com.example.registar.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.registar.model.Employee;
import com.example.registar.model.EmployeeWithRelations;

import java.util.List;
@Dao
public interface EmployeeDao {
    @Insert
    long insert(Employee employee);

    @Update
    void update(Employee employee);

    @Delete
    void delete(Employee employee);

    @Query("SELECT * FROM employee")
    List<Employee> getAll();

    @Transaction
    @Query("SELECT * FROM employee")
    List<EmployeeWithRelations> getAllWithDetails();
}
