package com.example.registar;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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

import com.example.registar.util.BitmapHelper;
import com.example.registar.util.CameraHelper;
import com.example.registar.model.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LocationEditActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Location location;
    private EditText cityView, addressView, latitudeView, longitudeView;
    private Marker currentMarker;
    private ImageView imageView;
    private String imagePath;

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

        // Get the map fragment and request the map to load
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        cityView = findViewById(R.id.city);
        addressView = findViewById(R.id.address);
        latitudeView = findViewById(R.id.latitude);
        longitudeView = findViewById(R.id.longitude);
        imageView = findViewById(R.id.icon);
        imageView.setOnClickListener(v -> CameraHelper.showImageOptions(this));

        location = (Location) getIntent().getSerializableExtra("clickedLocation");
        if (location != null) {
            cityView.setText(location.getCity());
            addressView.setText(location.getAddress());
            latitudeView.setText(String.valueOf(location.getLatitude()));
            longitudeView.setText(String.valueOf(location.getLongitude()));
            if (location.getImagePath() != null){
                imagePath = location.getImagePath();
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
            cancel(cityView);

        return super.onOptionsItemSelected(item);
    }

    public void cancel(View view) {
        CameraHelper.imagePath = null;
        finish();
    }

    public void saveLocation(View view) {
        if (!AssetEditActivity.validateInputs(Arrays.asList(cityView, addressView, latitudeView, longitudeView)))
            return;

        location.setCity(cityView.getText().toString().trim());
        location.setAddress(addressView.getText().toString().trim());
        location.setLatitude(Double.parseDouble(latitudeView.getText().toString()));
        location.setLongitude(Double.parseDouble(longitudeView.getText().toString()));
        if (CameraHelper.imagePath != null && !CameraHelper.imagePath.equals(imagePath)){
            location.setImagePath(CameraHelper.imagePath);
            CameraHelper.imagePath = null;
        }
        else
            location.setImagePath("no image");

        Intent resultIntent = new Intent();
        if (getTitle().equals(getString(R.string.edit_employee)))
            resultIntent.putExtra("updatedLocation", location);
        else
            resultIntent.putExtra("createdLocation", location);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng defaultLocation = new LatLng(44.7669, 17.1957);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        googleMap.setOnMapClickListener(latLng -> {
            if (currentMarker != null)
                currentMarker.remove();
            // Place a new marker
            currentMarker = googleMap.addMarker(new MarkerOptions().position(latLng));

            getCityName(latLng);
            latitudeView.setText(String.valueOf(latLng.latitude));
            longitudeView.setText(String.valueOf(latLng.longitude));
            latitudeView.setError(null);
            longitudeView.setError(null);
        });
    }

    private void getCityName(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                cityView.setText(addresses.get(0).getLocality());
                addressView.setText(addresses.get(0).getAddressLine(0));
                cityView.setError(null);
                addressView.setError(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}