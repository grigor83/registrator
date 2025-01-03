package com.example.registar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import com.example.registar.model.AssetList;
import com.example.registar.model.ListItem;
import com.example.registar.util.ExecutorHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class AssetListActivity extends AppCompatActivity {
    private AssetList assetList;
    private int position;
    private TextView assetListIdView;
    private ListView listView;

    private final ActivityResultLauncher<Intent> listItemActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    ListItem listItem = (ListItem) Objects.requireNonNull(result.getData()).getSerializableExtra("created");
                    if (listItem != null)
                        updateUI();
                }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_asset_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.census_lists_tab_name);

        assetListIdView = findViewById(R.id.asset_list_id);
        listView = findViewById(R.id.item_list);

        assetList = (AssetList) getIntent().getSerializableExtra("clickedAssetList");
        position = getIntent().getIntExtra("position", -1);
        if (assetList != null)
            updateUI();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListItemCreateActivity.class);
            intent.putExtra("clickedAssetList", assetList);
            listItemActivityLauncher.launch(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.popup, menu);
        menu.findItem(R.id.action_languages).setVisible(false);
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        MenuItem editItem = menu.findItem(R.id.action_edit);
        deleteItem.setVisible(true);
        editItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_languages)
            Toast.makeText(this, R.string.languages, Toast.LENGTH_SHORT).show();
        else if (item.getItemId() == R.id.action_delete){
            Intent resultIntent = new Intent();
            resultIntent.putExtra("position", position);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
        else if (item.getItemId() == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {
        assetListIdView.setText(String.valueOf(assetList.getId()));
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            ArrayList<ListItem> items = new ArrayList<>(MainActivity.registarDB.listItemDao()
                                        .getAllByAssetListId(assetList.getId()));
            ArrayAdapter<ListItem> adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.item, items);
            new Handler(Looper.getMainLooper()).post(() -> {
                listView.setAdapter(adapter);
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    Intent intent = new Intent(view.getContext(), ListItemActivity.class);
                    intent.putExtra("itemId", items.get(position).getId());
                    listItemActivityLauncher.launch(intent);
                });
            });
        });
    }
}