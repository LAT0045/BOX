package com.example.box;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment {
    private View view;

    private TextView locationText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI
        initializeUI();

        // Get location from intent
        String locationStr = getActivity().getIntent().getStringExtra("Address");
        locationText.setText(locationStr);

        return view;
    }

    private void initializeUI() {
        locationText = (TextView) view.findViewById(R.id.location_text);
    }
}