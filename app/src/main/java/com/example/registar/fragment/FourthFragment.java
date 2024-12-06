package com.example.registar.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.registar.R;

public class FourthFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentLayout = inflater.inflate(R.layout.fragment_fourth, container, false);
        AutoCompleteTextView autoCompleteTextView = fragmentLayout.findViewById(R.id.location);
        String[] options = {"Option 1", "Option 2", "Option 3", "Option 4", "Option 5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(fragmentLayout.getContext(), android.R.layout.simple_spinner_dropdown_item, options);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnClickListener(v -> {
            autoCompleteTextView.showDropDown(); // Force the dropdown to appear when clicked
        });

        return fragmentLayout;
    }
}