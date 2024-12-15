package com.example.registar.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.registar.MainActivity;
import com.example.registar.R;
import com.example.registar.adapter.AssetListAdapter;
import com.example.registar.model.AssetList;
import com.example.registar.util.ExecutorHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class FourthFragment extends Fragment {
    private List<AssetList> assetLists;
    private AssetListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityResultLauncher<Intent> assetListActivityLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        int position = Objects.requireNonNull(result.getData()).getIntExtra("position", -1);
                        if (position != -1) {
                            AssetList assetList = adapter.deleteAssetListFromList(position);
                            deleteAssetList(assetList);
                        }
                    } else
                        adapter.notifyDataSetChanged();
                });

        assetLists = new ArrayList<>();
        adapter = new AssetListAdapter(assetListActivityLauncher, assetLists);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentLayout = inflater.inflate(R.layout.fragment_fourth, container, false);
        RecyclerView recyclerView = fragmentLayout.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(fragmentLayout.getContext()));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = fragmentLayout.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            insertAssetList();
        });

        return fragmentLayout;
    }

    @Override
    public void onResume() {
        adapter.refresh();
        loadAssetLists();
        super.onResume();
    }

    private void loadAssetLists() {
        assetLists.clear();
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                assetLists.addAll(MainActivity.registarDB.assetListDao().getAll());
                adapter.refresh();
            });
        });
    }

    private void insertAssetList() {
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            AssetList assetList = new AssetList();
            long id = MainActivity.registarDB.assetListDao().insert(assetList);
            assetList.setId((int) id);
            new Handler(Looper.getMainLooper()).post(() -> {
                adapter.addAssetListToList(assetList);
                MainActivity.showCustomToast(getContext(),
                        getString(R.string.toast_create_message));
            });
        });
    }

    public static void deleteAssetList(AssetList assetList) {
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            MainActivity.registarDB.assetListDao().delete(assetList);
        });
    }
}