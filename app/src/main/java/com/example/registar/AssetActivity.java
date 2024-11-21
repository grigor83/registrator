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
        else if (item.getItemId() == R.id.action_delete)
            Toast.makeText(this, R.string.popup_crud_textview_delete_text, Toast.LENGTH_SHORT).show();
        else if (item.getItemId() == R.id.action_edit){
            Intent intent = new Intent(this, AssetActivityEditable.class);
            intent.putExtra("clickedAsset", asset);
            editActivityLauncher.launch(intent);
        }
        else if (item.getItemId() == android.R.id.home)
            this.finish();

        return super.onOptionsItemSelected(item);
    }

    private void updateUI(){
        titleTextview.setText(asset.getTitle());
        descriptionTextview.setText(asset.getDescription());
        creationDateTextview.setText(asset.getCreationDate().toString());
        priceTextview.setText(String.valueOf(asset.getPrice()));
        employeeTextview.setText(asset.getEmployee().getFullName());
        barcodeTextview.setText(String.valueOf(asset.getBarcode()));
        locationTextview.setText(asset.getLocation().getCity());
    }
}