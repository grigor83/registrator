package com.example.registar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
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

import com.example.registar.model.Asset;
import com.example.registar.model.Location;
import com.example.registar.util.BitmapHelper;
import com.example.registar.model.AssetWithRelations;
import com.example.registar.util.ExecutorHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class AssetActivity extends AppCompatActivity implements OnMapReadyCallback {
    private AssetWithRelations assetWithRelations;
    private int position;
    private TextView titleTextview, descriptionTextview,priceTextview, employeeTextview,
            locationTextview, creationDateTextview, barcodeTextview;
    private ImageView imageView;
    private boolean shouldReturn = false;
    private final ActivityResultLauncher<Intent> editActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    assetWithRelations = (AssetWithRelations) Objects.requireNonNull(result.getData()).getSerializableExtra("updatedAsset");
                    shouldReturn = true;
                    updateUI();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_asset);
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

        AssetWithRelations clickedAsset = (AssetWithRelations) getIntent().getSerializableExtra("clickedAsset");
        if (clickedAsset != null) {
            assetWithRelations = clickedAsset;
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
            if (mapFragment != null)
                mapFragment.getMapAsync(this);
            updateUI();
        }
        position = getIntent().getIntExtra("position", -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.popup, menu);

        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        MenuItem editItem = menu.findItem(R.id.action_edit);
        deleteItem.setVisible(true);
        editItem.setVisible(true);
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_languages).setVisible(false);

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
            Intent intent = new Intent(this, AssetEditActivity.class);
            intent.putExtra("clickedAsset", assetWithRelations);
            editActivityLauncher.launch(intent);
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

    private void updateUI(){
        titleTextview.setText(assetWithRelations.getAsset().getTitle().trim());
        descriptionTextview.setText(assetWithRelations.getAsset().getDescription().trim());
        if (null != assetWithRelations.getLocation())
            locationTextview.setText(assetWithRelations.getLocation().getCity().trim());
        if (null != assetWithRelations.getEmployee())
            employeeTextview.setText(assetWithRelations.getEmployee().getFullName().trim());
        creationDateTextview.setText(assetWithRelations.getAsset().getCreationDate().toString().trim());
        priceTextview.setText(String.valueOf(assetWithRelations.getAsset().getPrice()));
        barcodeTextview.setText(String.valueOf(assetWithRelations.getAsset().getBarcode()));
        if (assetWithRelations.getAsset().getImagePath() != null){
            File file = new File(assetWithRelations.getAsset().getImagePath());
            if (file.exists()){
                Uri imageUri = Uri.fromFile(file);
                imageView.post(() -> {
                    BitmapHelper.processImageInBackground(this, imageView, imageUri, true);
                });
            }
        }
    }

    private void putAssetToResultIntent(){
        if (shouldReturn){
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedAsset", assetWithRelations);
            setResult(RESULT_OK, resultIntent);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Location location = assetWithRelations.getLocation();
        LatLng objectLocation = new LatLng(location.getLatitude(), location.getLongitude());
        Marker marker = googleMap.addMarker(new MarkerOptions().position(objectLocation));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(objectLocation, 15));
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        googleMap.setOnMarkerClickListener(clickedMarker -> {
            if (clickedMarker.equals(marker)){
                showPopupWindow(findViewById(R.id.description));
                return true;
            }

            return false; // Return false to allow the default behavior (camera moves to marker)
        });
    }

    public void showPopupWindow(View anchorView) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_list_assets, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            ListView listView = popupView.findViewById(R.id.popup_list);
            ArrayList<Asset> locations = new ArrayList<>(MainActivity.registarDB.assetDao()
                                            .getAllByLocationId(assetWithRelations.getAsset().getLocationId()));
            ArrayAdapter<Asset> adapter = new ArrayAdapter<>(anchorView.getContext(), android.R.layout.simple_list_item_1, locations);
            listView.setAdapter(adapter);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                popupWindow.showAtLocation(anchorView, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                //popupWindow.showAsDropDown(anchorView);
            });
        });

    }
}