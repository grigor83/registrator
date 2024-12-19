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

import com.example.registar.util.BitmapHelper;
import com.example.registar.model.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.Objects;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Location location;
    private int position;
    private boolean shouldReturn = false;
    private TextView cityView, addressView, latitudeView, longitudeView;
    private ImageView imageView;

    private final ActivityResultLauncher<Intent> editLocationLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    location = (Location) Objects.requireNonNull(result.getData()).getSerializableExtra("updatedLocation");
                    shouldReturn = true;
                    updateUI();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_location);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.locations_tab_name);

        // Get the map fragment and request the map to load
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        cityView = findViewById(R.id.city);
        addressView = findViewById(R.id.address);
        latitudeView = findViewById(R.id.latitude);
        longitudeView = findViewById(R.id.longitude);
        imageView = findViewById(R.id.icon);

        location = (Location) getIntent().getSerializableExtra("clickedLocation");
        if (location != null)
            updateUI();
        position = getIntent().getIntExtra("position", -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.popup, menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_languages).setVisible(false);

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
            Intent intent = new Intent(this, LocationEditActivity.class);
            intent.putExtra("clickedLocation", location);
            editLocationLauncher.launch(intent);
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

    private void putAssetToResultIntent() {
        if (shouldReturn){
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedLocation", location);
            setResult(RESULT_OK, resultIntent);
        }
    }

    private void updateUI() {
        cityView.setText(location.getCity().trim());
        addressView.setText(location.getAddress().trim());
        latitudeView.setText(String.valueOf(location.getLatitude()));
        longitudeView.setText(String.valueOf(location.getLongitude()));
        if (location.getImagePath() != null){
            File file = new File(location.getImagePath());
            if (file.exists()){
                Uri imageUri = Uri.fromFile(file);
                imageView.post(() -> {
                    BitmapHelper.processImageInBackground(this, imageView, imageUri, true);
                });
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng objectLocation = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(objectLocation));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(objectLocation, 15));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }
}