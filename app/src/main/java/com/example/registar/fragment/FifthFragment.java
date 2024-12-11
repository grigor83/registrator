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

import com.example.registar.DepartmentEditActivity;
import com.example.registar.MainActivity;
import com.example.registar.R;
import com.example.registar.adapter.DepartmentAdapter;
import com.example.registar.model.Department;
import com.example.registar.util.ExecutorHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class FifthFragment extends Fragment {
    private List<Department> departments;
    private DepartmentAdapter adapter;
    private ActivityResultLauncher<Intent> departmentActivityLauncher;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        departmentActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        int position = Objects.requireNonNull(result.getData()).getIntExtra("position", -1);
                        if (position != -1) {
                            Department department = adapter.deleteDepartmentFromList(position);
                            deleteDepartment(department);
                            return;
                        }

                        Department department = (Department) Objects.requireNonNull(result.getData()).getSerializableExtra("createdDepartment");
                        if (department != null) {
                            adapter.addDepartmentToList(department);
                            insertDepartment(department);
                            return;
                        }

                        department = (Department) Objects.requireNonNull(result.getData()).getSerializableExtra("updatedDepartment");
                        if (department != null) {
                            adapter.replaceDepartmentInList(department);
                            updateDepartment(department);
                        }
                    }
                    else
                        adapter.notifyDataSetChanged();
                });

        departments = new ArrayList<>();
        adapter = new DepartmentAdapter(departmentActivityLauncher, departments);

    }

    @Override
    public void onResume() {
        adapter.refresh();
        loadDepartments();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentLayout = inflater.inflate(R.layout.fragment_fifth, container, false);

        RecyclerView recyclerView = fragmentLayout.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(fragmentLayout.getContext()));
        recyclerView.setAdapter(adapter);

        EditText departmentSearch = fragmentLayout.findViewById(R.id.departmentSearch);

        departmentSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filter(departmentSearch, adapter);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        FloatingActionButton fab = fragmentLayout.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DepartmentEditActivity.class);
            departmentActivityLauncher.launch(intent);
            adapter.highlightedItemPosition = -1;
        });

        return fragmentLayout;
    }

    private void loadDepartments() {
        departments.clear();
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                departments.addAll(MainActivity.registarDB.departmentDao().getAll());
                adapter.refresh();
            });
        });
    }
    public static void deleteDepartment(Department department) {
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            MainActivity.registarDB.departmentDao().delete(department);
        });
    }
    private void insertDepartment(Department department) {
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            long id = MainActivity.registarDB.departmentDao().insert(department);
            department.setId((int) id);
        });
    }
    private void updateDepartment(Department department) {
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            MainActivity.registarDB.departmentDao().update(department);
        });
    }
    private void filter(EditText departmentSearch, DepartmentAdapter adapter){
        String departmentQuery = departmentSearch.getText().toString();
        adapter.filter(departmentQuery);
    }
}