package com.example.box.FragmentAndActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.box.R;
import com.example.box.Entity.User;

public class CongratulationFragment extends Fragment {
    private View view;
    private FragmentManager fragmentManager;

    private User curUser;

    private ImageView backBtn;
    private RelativeLayout startButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_congratulation, container, false);

        // Initialize UI
        initializeUI();

        // Get user object from shared view model
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity())
                .get(SharedViewModel.class);
        sharedViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                curUser = user;
            }
        });


        // Get fragment manager
        fragmentManager = getActivity().getSupportFragmentManager();

        // Click start button
        clickStart();

        // Click back button
        clickBack();

        return view;
    }

    private void initializeUI() {
        backBtn = view.findViewById(R.id.btn_back);
        startButton = view.findViewById(R.id.start_button);
    }

    private void clickStart() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Home.class);
                startActivity(intent);
            }
        });
    }

    private void clickBack() {
        backBtn.setOnClickListener(v -> {
            // Get SignUpUpdateInfoFragment from back stack using "signUpInfoFragment" tag
            UpdateFragment updateFragment = (UpdateFragment) fragmentManager
                    .findFragmentByTag("updateFragment");

            // Replace current fragment with update fragment and not add to back stack
            fragmentManager.beginTransaction()
                    .replace(R.id.container, updateFragment, "updateFragment")
                    .commit();
        });
    }
}