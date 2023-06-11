package com.example.box.FragmentAndActivity;

import android.os.Bundle;
import android.os.Parcelable;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.box.Entity.Product;
import com.example.box.R;

import java.util.ArrayList;
import java.util.List;

public class Order extends AppCompatActivity {
    private FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Initialize UI
        initializeUI();

        // Get all items in carts
        List<Product> productList = new ArrayList<>();
        productList = getIntent().getParcelableArrayListExtra("checkOutList");

        // Initialize Fragment Manager
        fragmentTransaction(productList);
    }

    private void initializeUI() {
        container = findViewById(R.id.container);
    }

    private void fragmentTransaction(List<Product> productList) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (productList.size() > 0)
        {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("checkOutList",
                    (ArrayList<? extends Parcelable>) productList);

            OrderDetailFragment orderDetailFragment = new OrderDetailFragment();
            orderDetailFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.container, orderDetailFragment, "Order Detail")
                    .addToBackStack("Order Detail")
                    .commit();
        }

        else
        {
            // User's cart is empty
            EmptyOrderFragment emptyOrderFragment = new EmptyOrderFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, emptyOrderFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}