package com.example.registar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.registar.model.Asset;
import com.example.registar.model.Employee;
import com.example.registar.model.Location;

import java.io.File;
import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AssetCreateActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> pickImageLauncher;
    Asset asset;
    EditText titleTextview, descriptionTextview,priceTextview, employeeTextview, locationTextview;
    TextView creationDateTextview, barcodeTextview;
    ImageView imageView;


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

        titleTextview = findViewById(R.id.title);
        descriptionTextview = findViewById(R.id.description);
        creationDateTextview = findViewById(R.id.creation_date);
        priceTextview = findViewById(R.id.price);
        employeeTextview = findViewById(R.id.employee);
        locationTextview = findViewById(R.id.location);
        barcodeTextview = findViewById(R.id.barcode);
        imageView = findViewById(R.id.icon);

        creationDateTextview.setText(LocalDate.now().toString());

        // Set click listener for the ImageView
        ImageView imageView = findViewById(R.id.icon);
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        imageView.setImageURI(selectedImageUri); // Set the selected image to the ImageView
                    }
                }
        );

        MainActivity.takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK){
                        imageView.setImageURI(MainActivity.photoURI);
                        Log.i("kamera", String.valueOf(getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
                    }
                }
        );

        imageView.setOnClickListener(v -> {
            MainActivity.openCamera(this);
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            this.finish();

        return super.onOptionsItemSelected(item);
    }

    public void openImagePicker(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent); // Launch the image picker
    }

    public void cancel(View view) {
        finish();
    }

    public void saveAsset(View view) {
        if (!validateInputs())
            return;

        asset = new Asset();
        asset.setTitle(String.valueOf(titleTextview.getText()));
        asset.setDescription(String.valueOf(descriptionTextview.getText()));
        asset.setPrice((Integer.parseInt(priceTextview.getText().toString())));
        asset.setLocation(new Location((String.valueOf(locationTextview.getText()))));
        asset.setEmployee(new Employee(employeeTextview.getText().toString(), "nesto", "direktor"));
        asset.setCreationDate(LocalDate.parse(creationDateTextview.getText()));
        asset.setBarcode(45);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("createdAsset", asset);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (titleTextview.getText().toString().trim().isEmpty()) {
            titleTextview.setError(getString(R.string.error_text));
            titleTextview.requestFocus();
            return false;
        }

        if (descriptionTextview.getText().toString().trim().isEmpty()) {
            descriptionTextview.setError(getString(R.string.error_text));
            descriptionTextview.requestFocus();
            return false;
        }

        if (locationTextview.getText().toString().trim().isEmpty()) {
            locationTextview.setError(getString(R.string.error_text));
            locationTextview.requestFocus();
            return false;
        }

        if (employeeTextview.getText().toString().trim().isEmpty()) {
            employeeTextview.setError(getString(R.string.error_text));
            employeeTextview.requestFocus();
            return false;
        }

        if (priceTextview.getText().toString().trim().isEmpty()) {
            priceTextview.setError(getString(R.string.error_text));
            priceTextview.requestFocus();
            return false;
        }

        if (barcodeTextview.getText().toString().trim().isEmpty()) {
            barcodeTextview.setError(getString(R.string.error_text));
            barcodeTextview.requestFocus();
            return false;
        }

        return isValid;
    }


}