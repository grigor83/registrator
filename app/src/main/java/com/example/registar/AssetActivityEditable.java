package com.example.registar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.registar.model.Employee;
import com.example.registar.model.FixedAsset;
import com.example.registar.model.Location;

import java.util.Objects;

public class AssetActivityEditable extends AppCompatActivity {
    private ActivityResultLauncher<Intent> pickImageLauncher;
    FixedAsset asset;
    EditText titleTextview, descriptionTextview,priceTextview, employeeTextview, locationTextview;
    TextView creationDateTextview, barcodeTextview;
    ImageView imageView;

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
        creationDateTextview = findViewById(R.id.creation_date);
        priceTextview = findViewById(R.id.price);
        employeeTextview = findViewById(R.id.employee);
        locationTextview = findViewById(R.id.location);
        barcodeTextview = findViewById(R.id.barcode);
        imageView = findViewById(R.id.icon);

        final FixedAsset retrievedAsset = (FixedAsset) getIntent().getSerializableExtra("clickedAsset");
        if (retrievedAsset != null) {
            asset = retrievedAsset;
            titleTextview.setText(asset.getTitle());
            descriptionTextview.setText(asset.getDescription());
            creationDateTextview.setText(asset.getCreationDate().toString());
            priceTextview.setText(String.valueOf(asset.getPrice()));
            employeeTextview.setText(asset.getEmployee().getFullName());
            barcodeTextview.setText(String.valueOf(asset.getBarcode()));
            locationTextview.setText(asset.getLocation().getCity());
        }

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

    public void cancelEditing(View view) {
        finish();
    }

    public void saveAsset(View view) {
        asset.setTitle(String.valueOf(titleTextview.getText()));
        asset.setDescription(String.valueOf(descriptionTextview.getText()));
        asset.setPrice((Integer.parseInt(priceTextview.getText().toString())));
        asset.setLocation(new Location((String.valueOf(locationTextview.getText()))));
        asset.getEmployee().setName((String.valueOf(employeeTextview.getText())));

        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedAsset", asset);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}