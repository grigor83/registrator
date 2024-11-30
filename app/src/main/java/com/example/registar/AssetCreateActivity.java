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
import com.example.registar.model.Employee;
import com.example.registar.model.Location;

import java.time.LocalDate;
import java.util.Objects;

public class AssetCreateActivity extends AppCompatActivity {
    private EditText titleTextview, descriptionTextview,priceTextview, employeeTextview, locationTextview;
    private TextView creationDateTextview, barcodeTextview;


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
        locationTextview = findViewById(R.id.location);
        employeeTextview = findViewById(R.id.employee);
        creationDateTextview = findViewById(R.id.creation_date);
        priceTextview = findViewById(R.id.price);
        barcodeTextview = findViewById(R.id.barcode);
        creationDateTextview.setText(LocalDate.now().toString());
        ImageView imageView = findViewById(R.id.icon);
        imageView.setOnClickListener(v -> ImageHelper.showImageOptions(this));

        ImageHelper.pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        ImageHelper.createFileFromUri(this, imageView, selectedImageUri);
                        //imageView.setImageURI(selectedImageUri); // Set the selected image to the ImageView
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
        if (item.getItemId() == android.R.id.home)
            this.finish();

        return super.onOptionsItemSelected(item);
    }

    public void cancel(View view) {
        ImageHelper.imagePath = null;
        finish();
    }

    public void saveAsset(View view) {
        if (!validateInputs())
            return;

        Asset asset = new Asset();
        asset.setId(1002);
        asset.setTitle(String.valueOf(titleTextview.getText()));
        asset.setDescription(String.valueOf(descriptionTextview.getText()));
        asset.setPrice((Integer.parseInt(priceTextview.getText().toString())));
        asset.setLocation(new Location((String.valueOf(locationTextview.getText()))));
        asset.setEmployee(new Employee(employeeTextview.getText().toString(), "nesto", "direktor"));
        asset.setCreationDate(LocalDate.parse(creationDateTextview.getText()));
        asset.setBarcode(45);
        if (ImageHelper.imagePath != null){
            asset.setImagePath(ImageHelper.imagePath);
            ImageHelper.imagePath = null;
        }
        else
            asset.setImagePath("nema slike");

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