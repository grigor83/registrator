package com.example.registar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.registar.model.AssetList;
import com.example.registar.model.AssetWithRelations;
import com.example.registar.model.Employee;
import com.example.registar.model.ListItem;
import com.example.registar.model.Location;
import com.example.registar.util.ExecutorHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class ListItemCreateActivity extends AppCompatActivity {
    private AssetList assetList;
    private EditText oldEmployeeView, oldLocationView;
    private AutoCompleteTextView assetView, newEmployeeView, newLocationView;
    private AssetWithRelations selectedAsset;
    private Employee selectedEmployee;
    private Location selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_item_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.create_list_item);

        assetList = (AssetList) getIntent().getSerializableExtra("clickedAssetList");

        assetView = findViewById(R.id.asset);
        oldEmployeeView = findViewById(R.id.oldEmployee);
        newEmployeeView = findViewById(R.id.newEmployee);
        oldLocationView = findViewById(R.id.oldLocation);
        newLocationView = findViewById(R.id.newLocation);
        
        assetView.setOnClickListener(v -> {
            assetView.showDropDown();
        });
        assetView.setOnItemClickListener((parent, view, position, id) -> {
            selectedAsset = (AssetWithRelations) parent.getItemAtPosition(position);
            assetView.setError(null);
            setOldViews();
        });
        newEmployeeView.setOnClickListener(v -> {
            newEmployeeView.showDropDown();
        });
        newEmployeeView.setOnItemClickListener((parent, view, position, id) -> {
            selectedEmployee = (Employee) parent.getItemAtPosition(position);
            newEmployeeView.setError(null);
        });
        newLocationView.setOnClickListener(v -> {
            newLocationView.showDropDown();
        });
        newLocationView.setOnItemClickListener((parent, view, position, id) -> {
            selectedLocation = (Location) parent.getItemAtPosition(position);
            newLocationView.setError(null);
        });

        setAdapters();

    }

    private void setAdapters(){
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            ArrayList<AssetWithRelations> assets = new ArrayList<>(MainActivity.registarDB.assetDao().getAssetWithRelations());
            ArrayAdapter<AssetWithRelations> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, assets);
            ArrayList<Employee> employees = new ArrayList<>(MainActivity.registarDB.employeeDao().getAll());
            ArrayAdapter<Employee> adapter2 = new ArrayAdapter<>(this, R.layout.dropdown_item, employees);
            ArrayList<Location> locations = new ArrayList<>(MainActivity.registarDB.locationDao().getAll());
            ArrayAdapter<Location> adapter3 = new ArrayAdapter<>(this, R.layout.dropdown_item, locations);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                assetView.setAdapter(adapter);
                newEmployeeView.setAdapter(adapter2);
                newLocationView.setAdapter(adapter3);
            });
        });
    }

    private void setOldViews(){
        oldEmployeeView.setText(selectedAsset.getEmployee().getFullName());
        oldLocationView.setText(selectedAsset.getLocation().getCity());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public void cancel(View view) {
        finish();
    }

    public void saveListItem(View view) {
        if (!AssetEditActivity.validateInputs(Arrays.asList(assetView, newEmployeeView, newLocationView)))
            return;

        ListItem listItem = new ListItem();
        listItem.setAssetListId(assetList.getId());
        listItem.setAssetId(selectedAsset.getAsset().getId());
        listItem.setOldEmployeeId(selectedAsset.getEmployee().getId());
        listItem.setNewEmployeeId(selectedEmployee.getId());
        listItem.setOldLocationId(selectedAsset.getLocation().getId());
        listItem.setNewLocationId(selectedLocation.getId());

        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            MainActivity.registarDB.listItemDao().insert(listItem);
        });

        Intent resultIntent = new Intent();
        resultIntent.putExtra("created", listItem);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}