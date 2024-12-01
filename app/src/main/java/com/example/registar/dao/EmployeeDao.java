package com.example.registar.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.registar.model.Employee;

import java.util.List;
@Dao
public interface EmployeeDao {
    @Insert
    long insert(Employee employee);

    @Update
    void update(Employee employee);

    @Delete
    void delete(Employee employee);

    // Note... is varargs, here assets is an array
    @Delete
    void deleteEmployees(Employee... employees);

    @Query("SELECT * FROM employee")
    List<Employee> getAll();

    @Query("SELECT * FROM employee WHERE id = :employeeId")
    Employee getById(int employeeId);
}
