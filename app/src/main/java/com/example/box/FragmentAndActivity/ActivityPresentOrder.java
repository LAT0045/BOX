package com.example.box.FragmentAndActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.box.Entity.Product;
import com.example.box.R;

import java.util.ArrayList;
import java.util.List;

public class ActivityPresentOrder extends AppCompatActivity {

    List<Product> productList;
    RecyclerView apoRCV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present_order);
    }

    private void initializeUI()
    {
        apoRCV = (RecyclerView) findViewById(R.id.apo_rcv);
    }

    private void setupRCV()
    {
        productList = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        apoRCV.setLayoutManager(gridLayoutManager);
    }


}