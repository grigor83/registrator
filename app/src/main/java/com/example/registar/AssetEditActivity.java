package com.example.registar;

import android.annotation.SuppressLint;
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
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.registar.model.Employee;
import com.example.registar.model.EmployeeWithRelations;
import com.example.registar.util.BitmapHelper;
import com.example.registar.util.ExecutorHelper;
import com.example.registar.util.CameraHelper;
import com.example.registar.model.AssetWithRelations;
import com.example.registar.model.Location;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class AssetEditActivity extends AppCompatActivity {
    private AssetWithRelations asset;
    private EditText titleView, descriptionView, priceView, barcodeView;
    private AutoCompleteTextView locationView, employeeView;
    private Location selectedLocation;
    private EmployeeWithRelations selectedEmployee;
    private String imagePath;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_asset_editable);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.edit_employee);

        titleView = findViewById(R.id.title);
        descriptionView = findViewById(R.id.description);
        locationView = findViewById(R.id.location);
        locationView.setOnClickListener(v -> {
            locationView.showDropDown(); // Force the dropdown to appear when clicked
        });
        locationView.setOnItemClickListener((parent, view, position, id) -> {
            selectedLocation = (Location) parent.getItemAtPosition(position);
            locationView.setError(null);
        });
        employeeView = findViewById(R.id.employee);
        employeeView.setOnClickListener(v -> {
            employeeView.showDropDown(); // Force the dropdown to appear when clicked
        });
        employeeView.setOnItemClickListener((parent, view, position, id) -> {
            selectedEmployee = (EmployeeWithRelations) parent.getItemAtPosition(position);
            employeeView.setError(null);
        });
        setAdapters(locationView, employeeView);

        TextView creationDateTextview = findViewById(R.id.creation_date);
        priceView = findViewById(R.id.price);

        barcodeView = findViewById(R.id.barcode);
        barcodeView.setOnClickListener(v -> {
            if (!barcodeView.isFocusable()) {
                CameraHelper.showBarcodeInputDialog(barcodeView);
            }
        });

        ImageView imageView = findViewById(R.id.icon);
        imageView.setOnClickListener(v -> CameraHelper.showImageOptions(this));

        AssetWithRelations retrievedAsset = (AssetWithRelations) getIntent().getSerializableExtra("clickedAsset");
        if (retrievedAsset != null) {
            asset = retrievedAsset;
            titleView.setText(asset.getAsset().getTitle());
            descriptionView.setText(asset.getAsset().getDescription());
            if (null != asset.getLocation())
                locationView.setText(asset.getLocation().getCity());
            if (null != asset.getEmployee())
                employeeView.setText(asset.getEmployee().getFullName());
            creationDateTextview.setText(asset.getAsset().getCreationDate().toString());
            priceView.setText(String.valueOf(asset.getAsset().getPrice()));
            barcodeView.setText(String.valueOf(asset.getAsset().getBarcode()));
            imagePath = asset.getAsset().getImagePath();
            File file = new File(imagePath);
            if (file.exists()) {
                Uri imageUri = Uri.fromFile(file);
                // Dimensions of imageview are now available
                imageView.post(() -> {
                    BitmapHelper.processImageInBackground(this, imageView, imageUri, true);
                });
            }
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

        CameraHelper.scanBarcodeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null){
                        String scannedBarcode = result.getData().getStringExtra("SCAN_RESULT");
                        barcodeView.setText(scannedBarcode);
                    }
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            CameraHelper.imagePath = null;
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void cancelEditing(View view) {
        CameraHelper.imagePath = null;
        finish();
    }

    public void saveAsset(View view) {
        if (!validateInputs(Arrays.asList(titleView, descriptionView, locationView, employeeView, priceView, barcodeView)))
            return;

        asset.getAsset().setTitle(String.valueOf(titleView.getText()).trim());
        asset.getAsset().setDescription(String.valueOf(descriptionView.getText()).trim());
        if (null != selectedEmployee){
            asset.getAsset().setEmployeeId(selectedEmployee.getEmployee().getId());
            asset.setEmployee(selectedEmployee.getEmployee());
        }
        if (null != selectedLocation){
            asset.getAsset().setLocationId(selectedLocation.getId());
            asset.setLocation(selectedLocation);
        }
        asset.getAsset().setPrice((Integer.parseInt(priceView.getText().toString().trim())));
        try {
            String text = barcodeView.getText().toString();
            asset.getAsset().setBarcode(Long.parseLong(text));
        }
        catch (NumberFormatException e){
            MainActivity.showCustomToast(this, getString(R.string.error_converting_string));
            return;
        }

        if (CameraHelper.imagePath != null && !CameraHelper.imagePath.equals(imagePath)){
            CameraHelper.deleteOldImageFile(imagePath);
            asset.getAsset().setImagePath(CameraHelper.imagePath);
            CameraHelper.imagePath = null;
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedAsset", asset);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public static boolean validateInputs(List<EditText> list) {
        for (EditText view : list)
            if (!isValid(view))
                return false;

        return true;
    }

    private static boolean isValid(EditText view) {
        if (view.getText().toString().trim().isEmpty()) {
            view.setError(view.getContext().getString(R.string.error_text));
            view.requestFocus();
            return false;
        }
        else
            return true;
    }

    public static void setAdapters(AutoCompleteTextView locationView, AutoCompleteTextView employeeView) {
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            ArrayList<Location> locations = new ArrayList<>(MainActivity.registarDB.locationDao().getAll());
            ArrayAdapter<Location> adapter = new ArrayAdapter<>(locationView.getContext(), R.layout.dropdown_item, locations);
            ArrayList<Employee> employees = new ArrayList<>(MainActivity.registarDB.employeeDao().getAll());
            ArrayAdapter<Employee> adapter2 = new ArrayAdapter<>(employeeView.getContext(), R.layout.dropdown_item, employees);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                locationView.setAdapter(adapter);
                employeeView.setAdapter(adapter2);
            });
        });
    }
}