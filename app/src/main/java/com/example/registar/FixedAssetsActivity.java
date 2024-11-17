package com.example.registar;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.registar.model.Employee;
import com.example.registar.model.FixedAsset;
import com.example.registar.adapter.FixedAssetsAdapter;
import com.example.registar.model.Location;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FixedAssetsActivity extends AppCompatActivity {

    private final List<FixedAsset> assets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fixed_assets);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.fixedAssetsRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Employee m = new Employee("Jovan", "Jovanović", "magacioner");
        Employee k = new Employee("ivan", "Ivanković", "komercijalista");
        Location bl = new Location("Banjaluka");
        Location kv = new Location("Kotor Varoš");
        for (int i=0; i<100; i++){
            if (i%2 == 0)
                assets.add(new FixedAsset("stolica", "kancelarijska stolica", "slika", 555, 45, LocalDate.now(), m, bl));
            else
                assets.add(new FixedAsset("računar", "desktop računar", "slika", 1000, 500, LocalDate.now(), k, kv));
        }
        FixedAssetsAdapter adapter = new FixedAssetsAdapter(assets, this);
        recyclerView.setAdapter(adapter);

        EditText titleSearch = findViewById(R.id.titleSearch);
        titleSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filter(adapter);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        EditText locationSearch = findViewById(R.id.locationSearch);
        locationSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filter(adapter);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void filter(FixedAssetsAdapter adapter) {
        EditText titleSearch = findViewById(R.id.titleSearch);
        EditText locationSearch = findViewById(R.id.locationSearch);

        String titleQuery = titleSearch.getText().toString();
        String locationQuery = locationSearch.getText().toString();

        adapter.filter(titleQuery, locationQuery);
    }

    public void addNewAsset(View view) {

    }
}