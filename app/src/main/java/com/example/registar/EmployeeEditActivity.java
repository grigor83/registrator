package com.example.registar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.registar.model.Department;
import com.example.registar.model.EmployeeWithRelations;
import com.example.registar.util.BitmapHelper;
import com.example.registar.util.CameraHelper;
import com.example.registar.util.ExecutorHelper;
import com.example.registar.model.Employee;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class EmployeeEditActivity extends AppCompatActivity {
    private EmployeeWithRelations employee;
    private EditText firstnameView, lastnameView, salaryView;
    private AutoCompleteTextView departmentView;
    private ImageView imageView;
    private Department selectedDepartment;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        firstnameView = findViewById(R.id.firstname);
        lastnameView = findViewById(R.id.lastname);
        departmentView = findViewById(R.id.department);
        departmentView.setOnClickListener(v -> {
            departmentView.showDropDown();
        });
        departmentView.setOnItemClickListener((parent, view, position, id) -> {
            selectedDepartment = (Department) parent.getItemAtPosition(position);
            departmentView.setError(null);
        });
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            ArrayList<Department> departments = new ArrayList<>(MainActivity.registarDB.departmentDao().getAll());
            ArrayAdapter<Department> adapter = new ArrayAdapter<>(departmentView.getContext(), R.layout.dropdown_item, departments);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                departmentView.setAdapter(adapter);
            });
        });

        salaryView = findViewById(R.id.salary);
        imageView = findViewById(R.id.icon);
        imageView.setOnClickListener(v -> CameraHelper.showImageOptions(this));

        employee = (EmployeeWithRelations) getIntent().getSerializableExtra("clickedEmployee");
        if (employee != null) {
            firstnameView.setText(employee.getEmployee().getName());
            lastnameView.setText(employee.getEmployee().getLastName());
            if (employee.getDepartment() != null)
                departmentView.setText(employee.getDepartment().getName());
            salaryView.setText(String.valueOf(employee.getEmployee().getSalary()));
            if (employee.getEmployee().getImagePath() != null){
                imagePath = employee.getEmployee().getImagePath();
                File file = new File(imagePath);
                if (file.exists()) {
                    Uri imageUri = Uri.fromFile(file);
                    // Dimensions of imageview are now available
                    imageView.post(() -> {
                        BitmapHelper.processImageInBackground(this, imageView, imageUri, true);
                    });
                }
            }
            setTitle(R.string.edit_employee);
        }
        else{
            employee = new EmployeeWithRelations();
            employee.setEmployee(new Employee());
            setTitle(R.string.create_employee);
        }

        CameraHelper.pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        CameraHelper.createFileFromUri(this, imageView, selectedImageUri);
                    }
                }
        );

        CameraHelper.takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK)
                        BitmapHelper.processImageInBackground(this, imageView, CameraHelper.photoURI, true);
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            cancel(firstnameView);

        return super.onOptionsItemSelected(item);
    }

    public void cancel(View view) {
        CameraHelper.imagePath = null;
        finish();
    }

    public void saveEmployee(View view) {
        if (!AssetEditActivity.validateInputs(Arrays.asList(firstnameView, lastnameView, salaryView)))
            return;

        employee.getEmployee().setName(firstnameView.getText().toString().trim());
        employee.getEmployee().setLastName(lastnameView.getText().toString().trim());
        if (selectedDepartment != null){
            employee.getEmployee().setDepartmentId(selectedDepartment.getId());
            employee.setDepartment(selectedDepartment);
        }
        employee.getEmployee().setSalary(Double.parseDouble(salaryView.getText().toString().trim()));

        if (CameraHelper.imagePath != null && !CameraHelper.imagePath.equals(imagePath)){
            employee.getEmployee().setImagePath(CameraHelper.imagePath);
            CameraHelper.imagePath = null;
        }
        else
            employee.getEmployee().setImagePath("no image");

        Intent resultIntent = new Intent();
        if (getTitle().equals(getString(R.string.edit_employee)))
            resultIntent.putExtra("updatedEmployee", employee);
        else
            resultIntent.putExtra("createdEmployee", employee);

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}