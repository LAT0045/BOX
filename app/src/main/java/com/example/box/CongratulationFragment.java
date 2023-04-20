package com.example.box;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class CongratulationFragment extends Fragment {
    private View view;
    private FragmentManager fragmentManager;

    private ImageView backBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_congratulation, container, false);

        // Initialize UI
        initializeUI();

        // Get fragment manager
        fragmentManager = getActivity().getSupportFragmentManager();

        // Click back button
        clickBack();

        return view;
    }

    private void initializeUI() {
        backBtn = (ImageView) view.findViewById(R.id.btn_back);
    }

    private void clickBack() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get SignUpUpdateInfoFragment from back stack using "signUpInfoFragment" tag
                UpdateFragment updateFragment = (UpdateFragment) fragmentManager
                        .findFragmentByTag("updateFragment");

                // Replace current fragment with update fragment and not add to back stack
                fragmentManager.beginTransaction()
                        .replace(R.id.container, updateFragment, "updateFragment")
                        .commit();
            }
        });
    }
}