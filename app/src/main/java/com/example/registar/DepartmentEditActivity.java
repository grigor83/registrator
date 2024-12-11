package com.example.registar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.registar.model.Department;

import java.util.Collections;
import java.util.Objects;

public class DepartmentEditActivity extends AppCompatActivity {
    private Department department;
    private EditText departmentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_department_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        departmentView = findViewById(R.id.department);

        department = (Department) getIntent().getSerializableExtra("clickedDepartment");
        if (department != null) {
            departmentView.setText(department.getName());
            setTitle(R.string.edit_employee);
        }
        else{
            department = new Department();
            setTitle(R.string.create_department);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            cancel(departmentView);

        return super.onOptionsItemSelected(item);
    }


    public void cancel(View view) {
        finish();
    }

    public void saveDepartment(View view) {
        if (!AssetEditActivity.validateInputs(Collections.singletonList(departmentView)))
            return;

        department.setName(departmentView.getText().toString().trim());

        Intent resultIntent = new Intent();
        if (getTitle().equals(getString(R.string.edit_employee)))
            resultIntent.putExtra("updatedDepartment", department);
        else
            resultIntent.putExtra("createdDepartment", department);

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}