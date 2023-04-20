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

public class SignUpInfoFragment extends Fragment {
    private View view;
    private FragmentManager fragmentManager;

    private RelativeLayout signUpBtn;
    private ImageView backBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up_info, container, false);

        // Initialize UI
        initializeUI();

        // Get fragment manager
        fragmentManager = getActivity().getSupportFragmentManager();

        // Click sign up button
        clickSignUp();

        return view;
    }

    private void initializeUI() {
        signUpBtn = (RelativeLayout) view.findViewById(R.id.btn_sign_up);
        backBtn = (ImageView) view.findViewById(R.id.btn_back);
    }

    private void clickSignUp() {
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create OTP fragment
                SignUpOTPFragment signUpOTPFragment = new SignUpOTPFragment();

                // Replace current fragment with OTP fragment and not adding to back stack
                fragmentManager.beginTransaction()
                        .replace(R.id.container, signUpOTPFragment)
                        .commit();
            }
        });
    }

    private void clickBack() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}