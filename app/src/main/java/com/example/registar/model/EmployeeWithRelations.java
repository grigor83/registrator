package com.example.registar.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;

public class EmployeeWithRelations implements Serializable {
    @Embedded
    private Employee employee;
    @Relation(
            parentColumn = "departmentId",
            entityColumn = "id"
    )
    private Department department;

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
