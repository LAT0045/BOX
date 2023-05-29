package com.example.box;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.FrameLayout;

public class SignUp extends AppCompatActivity {
    private FrameLayout container;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        // Initialize UI
        initializeUI();

        // Initialize fragments
        SignUpInfoFragment signUpInfoFragment = new SignUpInfoFragment();

        // Initialize Fragment Manager
        fragmentManager = getSupportFragmentManager();

        // Make SignUpInfoFragment first fragment
        // Add to back stack with "signUpInfoFragment" tag
        fragmentManager.beginTransaction()
                .replace(R.id.container, signUpInfoFragment, "signUpInfoFragment")
                .addToBackStack("signUpInfoFragment")
                .commit();
    }

    private void initializeUI() {
        container = (FrameLayout) findViewById(R.id.container);
    }

}