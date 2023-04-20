package com.example.box;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class UpdateFragment extends Fragment {
    private View view;
    private FragmentManager fragmentManager;

    private ImageView backBtn;
    private RelativeLayout updateBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_update, container, false);

        // Initialize UI
        initializeUI();

        // Get fragment manager
        fragmentManager = getActivity().getSupportFragmentManager();

        // Click back button
        clickBack();

        // Click update button
        clickUpdate();

        return view;
    }

    private void initializeUI() {
        backBtn = (ImageView) view.findViewById(R.id.btn_back);
        updateBtn = (RelativeLayout) view.findViewById(R.id.btn_update);
    }

    private void clickBack() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create SignUpOTPFragment fragment
                SignUpOTPFragment signUpOTPFragment = new SignUpOTPFragment();

                // Replace current fragment with OTP fragment and not add to back stack
                fragmentManager.beginTransaction()
                        .replace(R.id.container, signUpOTPFragment)
                        .commit();
            }
        });
    }

    private void clickUpdate() {
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create SignUpCongrateFragment fragment
                CongratulationFragment congratulationFragment = new CongratulationFragment();

                // Replace current fragment with congratulation fragment and not add to back stack
                fragmentManager.beginTransaction()
                        .replace(R.id.container, congratulationFragment)
                        .commit();
            }
        });
    }
}