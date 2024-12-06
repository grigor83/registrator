package com.example.registar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.registar.helper.BitmapHelper;
import com.example.registar.helper.CameraHelper;
import com.example.registar.model.Location;

import java.util.Arrays;
import java.util.Objects;

public class LocationEditActivity extends AppCompatActivity {
    private Location location;
    private EditText city, address;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_location_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        city = findViewById(R.id.city);
        address = findViewById(R.id.address);

        imageView = findViewById(R.id.icon);
        imageView.setOnClickListener(v -> CameraHelper.showImageOptions(this));

        location = (Location) getIntent().getSerializableExtra("clickedLocation");
        if (location != null) {
            city.setText(location.getCity());
            address.setText(location.getAddress());
            /*
            if (employee.getImagePath() != null){
                imagePath = employee.getImagePath();
                File file = new File(imagePath);
                if (file.exists()) {
                    Uri imageUri = Uri.fromFile(file);
                    // Dimensions of imageview are now available
                    imageView.post(() -> {
                        BitmapHelper.processImageInBackground(this, imageView, imageUri, true);
                    });
                }
            }

             */
            setTitle(R.string.edit_employee);
        }
        else {
            location = new Location();
            setTitle(R.string.create_location);
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
            cancel(city);

        return super.onOptionsItemSelected(item);
    }

    public void cancel(View view) {
        CameraHelper.imagePath = null;
        finish();
    }

    public void saveLocation(View view) {
        if (!AssetEditActivity.validateInputs(Arrays.asList(city, address)))
            return;

        location.setCity(city.getText().toString().trim());
        location.setAddress(address.getText().toString().trim());
/*
        if (CameraHelper.imagePath != null && !CameraHelper.imagePath.equals(imagePath)){
            location.setImagePath(CameraHelper.imagePath);
            CameraHelper.imagePath = null;
        }

 */

        Intent resultIntent = new Intent();
        if (getTitle().equals(getString(R.string.edit_employee)))
            resultIntent.putExtra("updatedLocation", location);
        else
            resultIntent.putExtra("createdLocation", location);

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}