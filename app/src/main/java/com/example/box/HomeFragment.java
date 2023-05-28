package com.example.box;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    private View view;
    private User user;

    private TextView locationText;
    private ImageView locationMoreButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI
        initializeUI();

        // Get user from intent
        user = getActivity().getIntent().getParcelableExtra("user");

        // Set up this user's location
        String address = user.getAddress();
        locationText.setText(address);

        // Click more button in location
        clickMore();

        return view;
    }

    private void initializeUI() {
        locationText = (TextView) view.findViewById(R.id.location_text);
        locationMoreButton = (ImageView) view.findViewById(R.id.location_more_button);
    }

    private void clickMore() {
        locationMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationPicker locationPicker = new LocationPicker(getActivity(),
                        new LocationPicker.LocationConfirmationListener() {
                            @Override
                            public void onLocationConfirmed(String address) {
                                locationText.setText(address);
                            }
                        });
                locationPicker.loadMap();
            }
        });
    }
}