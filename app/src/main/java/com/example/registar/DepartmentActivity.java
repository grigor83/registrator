package com.example.registar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

import com.example.registar.model.Department;

import java.util.Objects;

public class DepartmentActivity extends AppCompatActivity {
    private Department department;
    private int position;
    private boolean shouldReturn = false;
    private TextView departmentView;

    private final ActivityResultLauncher<Intent> editActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    department = (Department) Objects.requireNonNull(result.getData()).getSerializableExtra("updatedDepartment");
                    shouldReturn = true;
                    updateUI();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_department);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.department_tab_name);

        departmentView = findViewById(R.id.department);

        department = (Department) getIntent().getSerializableExtra("clickedDepartment");
        if (department != null)
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
            Intent intent = new Intent(this, DepartmentEditActivity.class);
            intent.putExtra("clickedDepartment", department);
            editActivityLauncher.launch(intent);
        }
        else if (item.getItemId() == android.R.id.home){
            putDepartmentToResultIntent();
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        putDepartmentToResultIntent();
        super.onBackPressed();
    }

    private void updateUI() {
        departmentView.setText(department.getName().trim());
    }
    private void putDepartmentToResultIntent(){
        if (shouldReturn){
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedDepartment", department);
            setResult(RESULT_OK, resultIntent);
        }
    }
}