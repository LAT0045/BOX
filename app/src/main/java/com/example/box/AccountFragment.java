package com.example.box;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {
    private View view;
    private RelativeLayout logoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_account, container, false);

        //Initialize UI
        initializeUI();

        logOut();

        return view;
    }

    private void initializeUI() {
        logoutButton = (RelativeLayout) view.findViewById(R.id.logout_button);
    }

    private void logOut() {
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                FirebaseAuth.getInstance().signOut();
                loadLogIn();
            }
        });
    }

    private void loadLogIn() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }
}