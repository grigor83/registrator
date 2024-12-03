package com.example.registar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.registar.helper.BitmapHelper;
import com.example.registar.model.Employee;

import java.io.File;
import java.util.Objects;

public class EmployeeActivity extends AppCompatActivity {
    private Employee employee;
    private int position;
    private boolean shouldReturn = false;
    private TextView firstnameView, lastnameView, departmentView, salaryView;
    private ImageView imageView;
    private final ActivityResultLauncher<Intent> editActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    employee = (Employee) Objects.requireNonNull(result.getData()).getSerializableExtra("updatedEmployee");
                    shouldReturn = true;
                    updateUI();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.employers_tab_name);

        firstnameView = findViewById(R.id.firstname);
        lastnameView = findViewById(R.id.lastname);
        departmentView = findViewById(R.id.department);
        salaryView = findViewById(R.id.salary);
        imageView = findViewById(R.id.icon);

        employee = (Employee) getIntent().getSerializableExtra("clickedEmployee");
        if (employee != null)
            updateUI();
        position = getIntent().getIntExtra("position", -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.popup, menu);

        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        MenuItem editItem = menu.findItem(R.id.action_edit);
        deleteItem.setVisible(true);
        editItem.setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings)
            Toast.makeText(this, R.string.settings, Toast.LENGTH_SHORT).show();
        else if (item.getItemId() == R.id.action_languages)
            Toast.makeText(this, R.string.languages, Toast.LENGTH_SHORT).show();
        else if (item.getItemId() == R.id.action_delete){
            Intent resultIntent = new Intent();
            resultIntent.putExtra("position", position);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
        else if (item.getItemId() == R.id.action_edit){
            Intent intent = new Intent(this, EmployeeEditActivity.class);
            intent.putExtra("clickedEmployee", employee);
            editActivityLauncher.launch(intent);
        }
        else if (item.getItemId() == android.R.id.home){
            putAssetToResultIntent();
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        putAssetToResultIntent();
        super.onBackPressed();
    }

    private void updateUI() {
        firstnameView.setText(employee.getName().trim());
        lastnameView.setText(employee.getLastName().trim());
        departmentView.setText(employee.getDepartment().trim());
        salaryView.setText(String.valueOf(employee.getSalary()));
        if (employee.getImagePath() != null){
            File file = new File(employee.getImagePath());
            if (file.exists()){
                Uri imageUri = Uri.fromFile(file);
                imageView.post(() -> {
                    BitmapHelper.processImageInBackground(this, imageView, imageUri, true);
                });
            }
        }
    }
    private void putAssetToResultIntent(){
        if (shouldReturn){
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedEmployee", employee);
            setResult(RESULT_OK, resultIntent);
        }
    }
}