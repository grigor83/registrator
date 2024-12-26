package com.example.registar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.registar.model.ListItemWithDetails;
import com.example.registar.util.ExecutorHelper;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class ListItemActivity extends AppCompatActivity {
    private int itemId;
    private TextView assetView, newEmployeeView, newLocationView, oldEmployeeView, oldLocationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.list_item);

        assetView = findViewById(R.id.asset);
        oldEmployeeView = findViewById(R.id.oldEmployee);
        newEmployeeView = findViewById(R.id.newEmployee);
        oldLocationView = findViewById(R.id.oldLocation);
        newLocationView = findViewById(R.id.newLocation);

        itemId = getIntent().getIntExtra("itemId", -1);
        if (itemId != -1)
            updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.popup, menu);
        menu.findItem(R.id.action_languages).setVisible(false);

        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        MenuItem editItem = menu.findItem(R.id.action_edit);
        deleteItem.setVisible(false);
        editItem.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_languages)
            Toast.makeText(this, R.string.languages, Toast.LENGTH_SHORT).show();
        else if (item.getItemId() == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            ListItemWithDetails item = MainActivity.registarDB.listItemDao().getById(itemId);
            new Handler(Looper.getMainLooper()).post(() -> {
                assetView.setText(item.getAsset().getTitle());
                oldEmployeeView.setText(item.getOldEmployee().getFullName());
                newEmployeeView.setText(item.getNewEmployee().getFullName());
                oldLocationView.setText(item.getOldLocation().getCity());
                newLocationView.setText(item.getNewLocation().getCity());
            });
        });
    }
}