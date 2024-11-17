package com.example.registar.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.registar.R;
import com.example.registar.adapter.FixedAssetsAdapter;
import com.example.registar.model.Employee;
import com.example.registar.model.FixedAsset;
import com.example.registar.model.Location;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FragmentOne extends Fragment {

    private final List<FixedAsset> assets = new ArrayList<>();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public FragmentOne() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentOne.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentOne newInstance(String param1, String param2) {
        FragmentOne fragment = new FragmentOne();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_one, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.fixedAssetsRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        FixedAssetsAdapter adapter = new FixedAssetsAdapter(assets, view.getContext());
        recyclerView.setAdapter(adapter);

        EditText titleSearch = view.findViewById(R.id.titleSearch);
        titleSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filter(view, adapter);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        EditText locationSearch = view.findViewById(R.id.locationSearch);
        locationSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filter(view, adapter);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        return view;
    }

    private void filter(View view, FixedAssetsAdapter adapter) {
        EditText titleSearch = view.findViewById(R.id.titleSearch);
        EditText locationSearch = view.findViewById(R.id.locationSearch);

        String titleQuery = titleSearch.getText().toString();
        String locationQuery = locationSearch.getText().toString();

        adapter.filter(titleQuery, locationQuery);
    }
}