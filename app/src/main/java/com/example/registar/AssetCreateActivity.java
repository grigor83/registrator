package com.example.registar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

import com.example.registar.util.BitmapHelper;
import com.example.registar.util.CameraHelper;
import com.example.registar.model.Asset;
import com.example.registar.model.AssetWithRelations;
import com.example.registar.model.Employee;
import com.example.registar.model.Location;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

public class AssetCreateActivity extends AppCompatActivity {
    private EditText titleView, descriptionView, priceView, barcodeView;
    private AutoCompleteTextView locationView, employeeView;
    private TextView creationDateTextview;
    private Location selectedLocation;
    private Employee selectedEmployee;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_asset_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.assets_tab_name);

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
            selectedEmployee = (Employee) parent.getItemAtPosition(position);
            employeeView.setError(null);
        });
        AssetEditActivity.setAdapters(locationView, employeeView);
        creationDateTextview = findViewById(R.id.creation_date);
        creationDateTextview.setText(LocalDate.now().toString());
        priceView = findViewById(R.id.price);

        barcodeView = findViewById(R.id.barcode);
        barcodeView.setOnClickListener(v -> {
            if (!barcodeView.isFocusable()) {
                CameraHelper.showBarcodeInputDialog(barcodeView);
            }
        });

        ImageView imageView = findViewById(R.id.icon);
        imageView.setOnClickListener(v -> CameraHelper.showImageOptions(this));

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

    public void cancel(View view) {
        CameraHelper.imagePath = null;
        finish();
    }

    public void saveAsset(View view) {
        if (!AssetEditActivity.validateInputs(Arrays.asList(titleView, descriptionView, locationView, employeeView, priceView, barcodeView)))
            return;

        AssetWithRelations asset = new AssetWithRelations();
        asset.setAsset(new Asset());
        asset.getAsset().setTitle(String.valueOf(titleView.getText()));
        asset.getAsset().setDescription(String.valueOf(descriptionView.getText()));
        if (null != selectedEmployee){
            asset.getAsset().setEmployeeId(selectedEmployee.getId());
            asset.setEmployee(selectedEmployee);
        }
        if (null != selectedLocation){
            asset.getAsset().setLocationId(selectedLocation.getId());
            asset.setLocation(selectedLocation);
        }
        asset.getAsset().setCreationDate(LocalDate.parse(creationDateTextview.getText()));
        asset.getAsset().setPrice(Integer.parseInt(priceView.getText().toString()));
        try {
            String text = barcodeView.getText().toString();
            asset.getAsset().setBarcode(Long.parseLong(text));
        }
        catch (NumberFormatException e){
            MainActivity.showCustomToast(this, getString(R.string.error_converting_string));
            return;
        }
        if (CameraHelper.imagePath != null){
            asset.getAsset().setImagePath(CameraHelper.imagePath);
            CameraHelper.imagePath = null;
        }
        else
            asset.getAsset().setImagePath("no image");

        Intent resultIntent = new Intent();
        resultIntent.putExtra("createdAsset", asset);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}