package com.example.registar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.registar.model.FixedAsset;

import java.util.Objects;

public class AssetActivity extends AppCompatActivity {
    FixedAsset asset;
    int position;
    TextView titleTextview, descriptionTextview,priceTextview, employeeTextview,
            locationTextview, creationDateTextview, barcodeTextview;
    ImageView imageView;

    private final ActivityResultCallback<ActivityResult> resultCallback = result -> {
        if (result.getResultCode() == RESULT_OK) {
            asset = (FixedAsset) Objects.requireNonNull(result.getData()).getSerializableExtra("updatedAsset");
            updateUI();
        }
    };
    private final ActivityResultLauncher<Intent> editActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), resultCallback);

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

        FixedAsset retrievedAsset = (FixedAsset) getIntent().getSerializableExtra("clickedAsset");
        if (retrievedAsset != null) {
            asset = retrievedAsset;
            updateUI();
            switch (asset.getTitle()) {
                case "stolica":
                    imageView.setImageResource(R.drawable.chair);
                    break;
                case "računar":
                    imageView.setImageResource(R.drawable.computer_24);
                    break;
                default:
                    imageView.setImageResource(R.mipmap.ic_launcher);
                    break;
            }
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
            Intent intent = new Intent(this, AssetActivityEditable.class);
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
    }

    private void putAssetToResultIntent(){
        if (asset != null){
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedAsset", asset);
            setResult(RESULT_OK, resultIntent);
        }
    }
}