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

import com.example.registar.model.Asset;

import java.io.File;
import java.util.Objects;

public class AssetActivity extends AppCompatActivity {
    private Asset asset;
    private int position;
    private TextView titleTextview, descriptionTextview,priceTextview, employeeTextview,
            locationTextview, creationDateTextview, barcodeTextview;
    private ImageView imageView;
    boolean shouldReturn = false;

    private final ActivityResultLauncher<Intent> editActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    asset = (Asset) Objects.requireNonNull(result.getData()).getSerializableExtra("updatedAsset");
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

        Asset clickedAsset = (Asset) getIntent().getSerializableExtra("clickedAsset");
        if (clickedAsset != null) {
            asset = clickedAsset;
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
            intent.putExtra("clickedAsset", asset);
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
        titleTextview.setText(asset.getTitle().trim());
        descriptionTextview.setText(asset.getDescription().trim());
        creationDateTextview.setText(asset.getCreationDate().toString().trim());
        priceTextview.setText(String.valueOf(asset.getPrice()));
        employeeTextview.setText(asset.getEmployee().getFullName().trim());
        barcodeTextview.setText(String.valueOf(asset.getBarcode()).trim());
        locationTextview.setText(asset.getLocation().getCity().trim());
        File file = new File(asset.getImagePath());
        if (file.exists()){
            Uri imageUri = Uri.fromFile(file);
            BitmapHelper.processImageInBackground(this, imageView, imageUri);
            //imageView.setImageURI(imageUri);
            /*
            BitmapHelper.processImageInBackground(
                    this,
                    imageView.getWidth(),
                    imageView.getHeight(),
                    imageUri,
                    bitmap -> {
                        imageView.setImageBitmap(bitmap);
                    }
            );

             */
        }
    }

    private void putAssetToResultIntent(){
        if (shouldReturn){
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedAsset", asset);
            setResult(RESULT_OK, resultIntent);
        }
    }
}