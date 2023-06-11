package com.example.box.FragmentAndActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.example.box.R;

public class EmptyOrderFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_empty_order, container, false);

        ImageView backButton = view.findViewById(R.id.back_button);
        RelativeLayout startButton = view.findViewById(R.id.start_button);

        String storeId = requireActivity().getIntent().getStringExtra("storeId");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), StoreDetail.class);
                intent.putExtra("storeId", storeId);
                startActivity(intent);
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(requireActivity(), StoreDetail.class);
                intent.putExtra("storeId", storeId);
                startActivity(intent);
            }
        });

        return view;
    }
}