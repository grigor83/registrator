package com.example.registar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

import com.example.registar.helper.BitmapHelper;
import com.example.registar.helper.ImageHelper;
import com.example.registar.model.Asset;
import com.example.registar.model.Location;

import java.io.File;
import java.util.Objects;

public class AssetEditActivity extends AppCompatActivity {
    private Asset asset;
    private EditText titleTextview, descriptionTextview,priceTextview, employeeTextview, locationTextview;
    private TextView barcodeTextview;

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
        setTitle(R.string.assets_tab_name);

        titleTextview = findViewById(R.id.title);
        descriptionTextview = findViewById(R.id.description);
        locationTextview = findViewById(R.id.location);
        employeeTextview = findViewById(R.id.employee);
        TextView creationDateTextview = findViewById(R.id.creation_date);
        priceTextview = findViewById(R.id.price);
        barcodeTextview = findViewById(R.id.barcode);
        ImageView imageView = findViewById(R.id.icon);
        imageView.setOnClickListener(v -> ImageHelper.showImageOptions(this));

        final Asset retrievedAsset = (Asset) getIntent().getSerializableExtra("clickedAsset");
        if (retrievedAsset != null) {
            asset = retrievedAsset;
            titleTextview.setText(asset.getTitle());
            descriptionTextview.setText(asset.getDescription());
            locationTextview.setText(asset.getLocation().getCity());
            employeeTextview.setText(asset.getEmployee().getFullName());
            creationDateTextview.setText(asset.getCreationDate().toString());
            priceTextview.setText(String.valueOf(asset.getPrice()));
            barcodeTextview.setText(String.valueOf(asset.getBarcode()));
            File file = new File(asset.getImagePath());
            if (file.exists()) {
                Uri imageUri = Uri.fromFile(file);
                // Dimensions of imageview are now available
                imageView.post(() -> {
                    BitmapHelper.processImageInBackground(this, imageView, imageUri, true);
                });
            }
        }

        ImageHelper.pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        ImageHelper.createFileFromUri(this, imageView, selectedImageUri);
                    }
                }
        );

        ImageHelper.takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK)
                        BitmapHelper.processImageInBackground(this, imageView, ImageHelper.photoURI, true);
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            ImageHelper.imagePath = null;
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void cancelEditing(View view) {
        ImageHelper.imagePath = null;
        finish();
    }

    public void saveAsset(View view) {
        if (!validateInputs())
            return;

        asset.setTitle(String.valueOf(titleTextview.getText()).trim());
        asset.setDescription(String.valueOf(descriptionTextview.getText()).trim());
        asset.setPrice((Integer.parseInt(priceTextview.getText().toString().trim())));
        asset.setLocation(new Location((String.valueOf(locationTextview.getText())).trim()));
        asset.getEmployee().setName((String.valueOf(employeeTextview.getText())).trim());
        if (ImageHelper.imagePath != null){
            asset.setImagePath(ImageHelper.imagePath);
            ImageHelper.imagePath = null;
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedAsset", asset);
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