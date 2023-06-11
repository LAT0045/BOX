package com.example.box.FragmentAndActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.box.Entity.Category;
import com.example.box.Entity.CategoryAdapter;
import com.example.box.Entity.Product;
import com.example.box.Entity.ProductAdapter;
import com.example.box.R;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailFragment extends Fragment {
    private View view;
    private String storeId;

    private RecyclerView categoryRcv;
    private List<Product> checkOutProducts = new ArrayList<>();

    private TextView totalPriceText;

    private ImageView backButton;
    private RelativeLayout checkOutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_order_detail, container, false);

        // Initialize UI
        initializeUI();
        storeId = requireActivity().getIntent().getStringExtra("storeId");

        // Get the check out products
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            checkOutProducts = bundle.getParcelableArrayList("checkOutList");

            // Set total price
            totalPriceText.setText(Integer.toString(getTotalPrice()) + "Đ");
        }

        setUpRecyclerView();

        // Click check out button
        clickCheckOut();

        // Click back button
        clickBack();

        return view;
    }

    private void initializeUI() {
        categoryRcv = view.findViewById(R.id.category_rcv);
        totalPriceText = view.findViewById(R.id.total_price_text);

        backButton = view.findViewById(R.id.back_button);
        checkOutButton = view.findViewById(R.id.check_out_button);
    }

    private void setUpRecyclerView() {
        categoryRcv.setNestedScrollingEnabled(false);

        // Initialize category and product list
        List<Category<?>> categoryList = new ArrayList<Category<?>>();
        Category<Product> products = new Category<Product>("", checkOutProducts);
        categoryList.add(products);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(), 1,
                GridLayoutManager.VERTICAL, false );
        categoryRcv.setLayoutManager(gridLayoutManager);

        // Add this so the recycler view won't crash
        categoryRcv.setItemAnimator(null);

        // Set category to recyclerview
        CategoryAdapter categoryAdapter = new CategoryAdapter(requireActivity(),
                categoryList, new CategoryAdapter.CartChanged() {
            @Override
            public void onCartChanged(Product product, boolean isAdded) {
                int position = getExistedProductPosition(product.getProductId());

                // This is the product that has been added
                // Or removed from cart
                if (isAdded)
                {
                    // Check if the user has added this product or not
                    // The product isn't in cart if position is -1
                    if (position == -1)
                    {
                        // Add new product to cart
                        checkOutProducts.add(product);
                    }

                    else
                    {
                        // User already added this product
                        // Update the quantity
                        int quantity = checkOutProducts.get(position).getCurQuantity();
                        checkOutProducts.get(position).setCurQuantity(quantity);
                    }
                }

                else
                {
                    // Update the quantity when user remove a product
                    int quantity = checkOutProducts.get(position).getCurQuantity();
                    checkOutProducts.get(position).setCurQuantity(quantity);

                    // If quantity is 0 then remove the product
                    if (quantity == 0)
                    {
                        checkOutProducts.remove(position);
                    }
                }

                // Update price
                totalPriceText.setText(Integer.toString(getTotalPrice()) + "Đ");
            }
        });
        categoryAdapter.setType(ProductAdapter.PRODUCT_IN_STORE_TYPE);
        categoryRcv.setAdapter(categoryAdapter);
    }

    private int getExistedProductPosition(String productId) {
        int res = -1;

        if (checkOutProducts != null && checkOutProducts.size() != 0)
        {
            for(int i = 0; i < checkOutProducts.size(); i++)
            {
                if (checkOutProducts.get(i).getProductId().equals(productId))
                {
                    res = i;
                    break;
                }
            }
        }

        return res;
    }

    private int getTotalPrice() {
        int total = 0;
        for(Product product : checkOutProducts)
        {
            int toppingPrice = getToppingPrice(product.getToppingList());
            total += (product.getProductPrice() * product.getCurQuantity());
            total += toppingPrice;
        }
        return total;
    }

    private int getToppingPrice(List<Product> toppings) {
        int total = 0;
        for(Product product : toppings)
        {
            total += (product.getProductPrice() * product.getCurQuantity());
        }
        return total;
    }

    private void clickCheckOut() {
        checkOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("checkOutList",
                        (ArrayList<? extends Parcelable>) checkOutProducts);

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                CheckOutFragment checkOutFragment = new CheckOutFragment();
                checkOutFragment.setArguments(bundle);

                fragmentManager.beginTransaction()
                        .replace(R.id.container, checkOutFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void clickBack() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), StoreDetail.class);
                intent.putParcelableArrayListExtra("checkOutList",
                        (ArrayList<? extends Parcelable>) checkOutProducts);
                intent.putExtra("storeId",storeId);
                startActivity(intent);
            }
        });
    }
}