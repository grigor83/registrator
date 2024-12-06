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

import com.example.registar.EmployeeEditActivity;
import com.example.registar.MainActivity;
import com.example.registar.R;
import com.example.registar.adapter.EmployeeAdapter;
import com.example.registar.helper.ExecutorHelper;
import com.example.registar.model.Employee;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class SecondFragment extends Fragment {
    private List<Employee> employees;
    private EmployeeAdapter adapter;
    private ActivityResultLauncher<Intent> employeeActivityLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        employeeActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        int position = Objects.requireNonNull(result.getData()).getIntExtra("position", -1);
                        if (position != -1) {
                            Employee employee = adapter.deleteEmployeeFromList(position);
                            deleteEmployee(employee);
                            return;
                        }

                        Employee employee = (Employee) Objects.requireNonNull(result.getData()).getSerializableExtra("createdEmployee");
                        if (employee != null) {
                            adapter.addEmployeeToList(employee);
                            insertEmployee(employee);
                            return;
                        }

                        employee = (Employee) Objects.requireNonNull(result.getData()).getSerializableExtra("updatedEmployee");
                        if (employee != null) {
                            adapter.replaceEmployeeInList(employee);
                            updateEmployee(employee);
                        }
                    }
                    else
                        adapter.notifyDataSetChanged();
                });

        employees = new ArrayList<>();
        adapter = new EmployeeAdapter(employees, employeeActivityLauncher);
        //loadEmployees();
    }

    @Override
    public void onResume() {
        adapter.refresh();
        loadEmployees();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentLayout = inflater.inflate(R.layout.fragment_second, container, false);

        RecyclerView recyclerView = fragmentLayout.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(fragmentLayout.getContext()));
        recyclerView.setAdapter(adapter);

        EditText nameSearch = fragmentLayout.findViewById(R.id.nameSearch);
        EditText departmentSearch = fragmentLayout.findViewById(R.id.departmentSearch);

        nameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filter(nameSearch, departmentSearch, adapter);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        departmentSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filter(nameSearch, departmentSearch, adapter);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        FloatingActionButton fab = fragmentLayout.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EmployeeEditActivity.class);
            employeeActivityLauncher.launch(intent);
            adapter.highlightedItemPosition = -1;
        });

        return fragmentLayout;
    }

    private void loadEmployees() {
        employees.clear();
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                employees.addAll(MainActivity.registarDB.employeeDao().getAll());
                adapter.refresh();
            });
        });
    }
    public static void deleteEmployee(Employee employee) {
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            MainActivity.registarDB.employeeDao().delete(employee);
        });
    }
    private void insertEmployee(Employee employee) {
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            long id = MainActivity.registarDB.employeeDao().insert(employee);
            employee.setId((int) id);
        });
    }
    private void updateEmployee(Employee employee) {
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            MainActivity.registarDB.employeeDao().update(employee);
        });
    }
    private void filter(EditText nameSearch, EditText departmentSearch, EmployeeAdapter adapter){
        String nameQuery = nameSearch.getText().toString();
        String departmentQuery = departmentSearch.getText().toString();

        adapter.filter(nameQuery, departmentQuery);
    }


}