package com.example.registar.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.registar.LocationEditActivity;
import com.example.registar.MainActivity;
import com.example.registar.R;
import com.example.registar.adapter.LocationAdapter;
import com.example.registar.util.ExecutorHelper;
import com.example.registar.model.Location;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class ThirdFragment extends Fragment {
    private List<Location> locations;
    private LocationAdapter adapter;
    private ActivityResultLauncher<Intent> locationActivityLauncher;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        int position = Objects.requireNonNull(result.getData()).getIntExtra("position", -1);
                        if (position != -1) {
                            Location location = adapter.deleteLocationFromList(position);
                            deleteLocation(location);
                            return;
                        }

                        Location location = (Location) Objects.requireNonNull(result.getData()).getSerializableExtra("createdLocation");
                        if (location != null) {
                            adapter.addLocationToList(location);
                            insertLocation(location);
                            return;
                        }

                        location = (Location) Objects.requireNonNull(result.getData()).getSerializableExtra("updatedLocation");
                        if (location != null) {
                            adapter.replaceLocationInList(location);
                            updateLocation(location);
                        }
                    }
                    else
                        adapter.notifyDataSetChanged();
                });

        locations = new ArrayList<>();
        adapter = new LocationAdapter(locations, locationActivityLauncher);

    }

    @Override
    public void onResume() {
        adapter.refresh();
        loadLocations();
        super.onResume();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentLayout = inflater.inflate(R.layout.fragment_third, container, false);

        RecyclerView recyclerView = fragmentLayout.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(fragmentLayout.getContext()));
        recyclerView.setAdapter(adapter);

        EditText citySearch = fragmentLayout.findViewById(R.id.cityNameSearch);
        EditText addressSearch = fragmentLayout.findViewById(R.id.addressSearch);

        citySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filter(citySearch, addressSearch, adapter);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        addressSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filter(citySearch, addressSearch, adapter);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        FloatingActionButton fab = fragmentLayout.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LocationEditActivity.class);
            locationActivityLauncher.launch(intent);
            adapter.highlightedItemPosition = -1;
        });

        return fragmentLayout;
    }

    private void loadLocations() {
        locations.clear();
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                locations.addAll(MainActivity.registarDB.locationDao().getAll());
                adapter.refresh();
            });
        });
    }

    public static void deleteLocation(Location location) {
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            MainActivity.registarDB.locationDao().delete(location);
        });
    }

    private void updateLocation(Location location) {
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            MainActivity.registarDB.locationDao().update(location);
        });
    }

    private void insertLocation(Location location) {
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            long id = MainActivity.registarDB.locationDao().insert(location);
            location.setId((int) id);
        });
    }

    private void filter(EditText citySearch, EditText addressSearch, LocationAdapter adapter) {
        String cityQuery = citySearch.getText().toString();
        String addressQuery = addressSearch.getText().toString();

        adapter.filter(cityQuery, addressQuery);
    }


}