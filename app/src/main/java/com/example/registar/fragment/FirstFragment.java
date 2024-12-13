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

import com.example.registar.AssetCreateActivity;
import com.example.registar.MainActivity;
import com.example.registar.R;
import com.example.registar.adapter.AssetsAdapter;
import com.example.registar.util.ExecutorHelper;
import com.example.registar.model.AssetWithRelations;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class FirstFragment extends Fragment {

    private List<AssetWithRelations> assets;
    private AssetsAdapter adapter;
    private ActivityResultLauncher<Intent> createAssetLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Launcher za rezultat koji se vraća iz AssetActivity. Ovaj launcher se proslijedi Asset adapteru, koji onda pokreće AssetActivity pomoću njega.
        // Ako user u AssetActivity stisne dugme za brisanje iz toolbar-a, vratiće int position. Ako user stisne dugme edit, pa ode na AssetEditActivity
        // i u slučaju da napravi neke izmjene, te izmjene ce se vratiti prvo u AssetActivity, pa onda ovdje, tj. u AssetAdapter.
        ActivityResultLauncher<Intent> deleteAssetLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                int position = Objects.requireNonNull(result.getData()).getIntExtra("position", -1);
                if (position != -1) {
                    AssetWithRelations asset = adapter.deleteAssetFromList(position);
                    deleteAsset(asset);
                    return;
                }

                AssetWithRelations updatedAsset = (AssetWithRelations) Objects.requireNonNull(result.getData()).getSerializableExtra("updatedAsset");
                if (updatedAsset != null){
                    adapter.replaceAssetInList(updatedAsset);
                    updateAsset(updatedAsset);
                }
            }
            else
                adapter.notifyDataSetChanged();
        });

        createAssetLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        AssetWithRelations createdAsset = (AssetWithRelations) Objects.requireNonNull(result.getData()).getSerializableExtra("createdAsset");
                        if (createdAsset != null){
                            adapter.addAssetToList(createdAsset);
                            insertAsset(createdAsset);
                        }
                    }
                    else
                        adapter.notifyDataSetChanged();
                });

        assets = new ArrayList<>();
        adapter = new AssetsAdapter(assets, deleteAssetLauncher);
    }

    @Override
    public void onResume() {
        adapter.refresh();
        loadAssets();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentLayout = inflater.inflate(R.layout.fragment_first, container, false);

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

    private void loadAssets() {
        assets.clear();
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                assets.addAll(MainActivity.registarDB.assetDao().getAssetWithRelations());
                adapter.refresh();
            });
        });
    }

    private void deleteAsset(AssetWithRelations assetWithRelations) {
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            MainActivity.registarDB.assetDao().delete(assetWithRelations.getAsset());
        });
    }

    private void updateAsset(AssetWithRelations updatedAsset) {
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            MainActivity.registarDB.assetDao().update(updatedAsset.getAsset());
        });
    }

    private void insertAsset(AssetWithRelations createdAsset) {
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            long id = MainActivity.registarDB.assetDao().insert(createdAsset.getAsset());
            createdAsset.getAsset().setId((int) id);
        });
    }

}