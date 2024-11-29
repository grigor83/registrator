package com.example.registar.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.registar.AssetCreateActivity;
import com.example.registar.R;
import com.example.registar.adapter.AssetsAdapter;
import com.example.registar.model.Asset;
import com.example.registar.model.Employee;
import com.example.registar.model.Location;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FirstFragment extends Fragment {

    private final List<Asset> assets = new ArrayList<>();
    private AssetsAdapter adapter;

    //Launcher za rezultat koji se vraća iz AssetActivity. Ovaj launcher se proslijedi Asset adapteru, koji onda pokreće AssetActivity pomoću njega.
    // Ako user u AssetActivity stisne dugme za brisanje iz toolbar-a, vratiće int position. Ako user stisne dugme edit, pa ode na AssetEditActivity
    // i u slučaju da napravi neke izmjene, te izmjene ce se vratiti prvo u AssetActivity, pa onda ovdje, tj. u AssetAdapter.
    private final ActivityResultLauncher<Intent> deleteAssetLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    int position = Objects.requireNonNull(result.getData()).getIntExtra("position", -1);
                    if (position != -1)
                        adapter.deleteAssetFromList(position);

                    Asset updatedAsset = (Asset) Objects.requireNonNull(result.getData()).getSerializableExtra("updatedAsset");
                    if (updatedAsset != null)
                        adapter.replaceAssetInList(updatedAsset);

                }
                else
                    adapter.notifyDataSetChanged();
            });

    private final ActivityResultLauncher<Intent> createAssetLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Asset createdAsset = (Asset) Objects.requireNonNull(result.getData()).getSerializableExtra("createdAsset");
                    if (createdAsset != null)
                        adapter.addAssetToList(createdAsset);
                }
                else
                    adapter.notifyDataSetChanged();
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Employee m = new Employee("Jovan", "Jovanović", "magacioner");
        Employee k = new Employee("ivan", "Ivanković", "komercijalista");
        Location bl = new Location("Banjaluka");
        Location kv = new Location("Kotor Varoš");
        for (int i=0; i<17; i++){
            if (i%2 == 0)
                assets.add(new Asset(i, "stolica"+i, "kancelarijska stolica", "slika",
                        555, 45, LocalDate.now(), m, bl));
            else
                assets.add(new Asset(i, "računar"+i, "desktop računar", "slika",
                        1000, 500, LocalDate.now(), k, kv));
        }

        adapter = new AssetsAdapter(assets, deleteAssetLauncher);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentLayout = inflater.inflate(R.layout.first_fragment, container, false);

        RecyclerView recyclerView = fragmentLayout.findViewById(R.id.fixedAssetsRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(fragmentLayout.getContext()));
        recyclerView.setAdapter(adapter);

        EditText titleSearch = fragmentLayout.findViewById(R.id.titleSearch);
        titleSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filter(fragmentLayout, adapter);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        EditText locationSearch = fragmentLayout.findViewById(R.id.locationSearch);
        locationSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filter(fragmentLayout, adapter);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        FloatingActionButton fab = fragmentLayout.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AssetCreateActivity.class);
            createAssetLauncher.launch(intent);
            adapter.highlightedItemPosition = -1;
        });

        return fragmentLayout;
    }

    private void filter(View view, AssetsAdapter adapter) {
        EditText titleSearch = view.findViewById(R.id.titleSearch);
        EditText locationSearch = view.findViewById(R.id.locationSearch);

        String titleQuery = titleSearch.getText().toString();
        String locationQuery = locationSearch.getText().toString();

        adapter.filter(titleQuery, locationQuery);
    }

}