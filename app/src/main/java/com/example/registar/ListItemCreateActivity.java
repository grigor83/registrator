package com.example.registar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.registar.model.AssetWithRelations;
import com.example.registar.model.ListItem;
import com.example.registar.util.ExecutorHelper;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class ListItemCreateActivity extends AppCompatActivity {
    private ListItem listItem;
    private AutoCompleteTextView oldAssetView, newAssetView, oldEmployeeView, newEmployeeView,
            oldLocationView, newLocationView;
    private AssetWithRelations oldAsset, newAsset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_item_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.create_list_item);

        oldAssetView = findViewById(R.id.oldAssetTitle);
        newAssetView = findViewById(R.id.newAssetTitle);
        oldEmployeeView = findViewById(R.id.oldEmployee);
        newEmployeeView = findViewById(R.id.newEmployee);
        oldLocationView = findViewById(R.id.oldLocation);
        newLocationView = findViewById(R.id.newLocation);
        
        oldAssetView.setOnClickListener(v -> {
            oldAssetView.showDropDown();
        });
        oldAssetView.setOnItemClickListener((parent, view, position, id) -> {
            oldAsset = (AssetWithRelations) parent.getItemAtPosition(position);
            oldAssetView.setError(null);
            setOldViews();
        });
        newAssetView.setOnClickListener(v -> {
            newAssetView.showDropDown();
        });
        newAssetView.setOnItemClickListener((parent, view, position, id) -> {
            newAsset = (AssetWithRelations) parent.getItemAtPosition(position);
            newAssetView.setError(null);

        });

        setAdapters();

    }

    private void setAdapters(){
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            ArrayList<AssetWithRelations> assets = new ArrayList<>(MainActivity.registarDB.assetDao().getAssetWithRelations());
            ArrayAdapter<AssetWithRelations> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, assets);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                oldAssetView.setAdapter(adapter);
                newAssetView.setAdapter(adapter);
            });
        });
    }

    private void setOldViews(){
        oldEmployeeView.setText(oldAsset.getEmployee().getFullName());
        oldLocationView.setText(oldAsset.getLocation().getCity());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public void cancel(View view) {
        finish();
    }

    public void saveListItem(View view) {
    }
}