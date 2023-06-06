package com.example.box.FragmentAndActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.box.Entity.AsyncResponse;
import com.example.box.Entity.Category;
import com.example.box.Entity.CategoryAdapter;
import com.example.box.Entity.DataHandler;
import com.example.box.Entity.Product;
import com.example.box.Entity.ProductAdapter;
import com.example.box.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StoreDetail extends AppCompatActivity {
    private String storeId;

    private ImageView storeImage;
    private TextView storeName;
    private TextView storeAddress;
    private TextView time;
    private TextView storeRating;

    private RecyclerView categoryRcv;
    private List<Product> productList;

    private ImageView backButton;
    private RelativeLayout checkOutButton;

    private int totalTasks = 2;
    private int finishedTasks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        Log.d("IN STORE DETAIL", "HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEE");

        // Get the current store's id
        storeId = getIntent().getStringExtra("storeId");
        Log.d("IN STORE DETAIL", storeId);

        // Initialize UI
        initializeUI();

        getStoreById();

        getStoreProducts();

        clickBack();
    }

    private void initializeUI() {
        storeImage = findViewById(R.id.store_image);
        storeName = findViewById(R.id.store_name);
        storeAddress = findViewById(R.id.store_address);
        time = findViewById(R.id.time);
        storeRating = findViewById(R.id.store_rating); //TODO: Change rating type

        categoryRcv = findViewById(R.id.category_rcv);

        backButton = findViewById(R.id.back_button);
        checkOutButton = findViewById(R.id.check_out_button);
    }

    private void getStoreById() {
        String urlStr = "/box/getStoreById.php";

        DataHandler dataHandler = new DataHandler(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (!output.equals("NO"))
                {
                    try
                    {
                        JSONArray jsonArray = new JSONArray(output);

                        // Iterate over the JSON array
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String image = jsonObject.getString("hinhanh");
                            String name = jsonObject.getString("tencuahang");
                            String address = jsonObject.getString("diachi");
//                            double rating = jsonObject.isNull("chatluong")
//                                    ? null : jsonObject.getDouble("chatluong");

                            Picasso.get()
                                    .load(image)
                                    .into(storeImage);

                            storeName.setText(name);
                            storeAddress.setText(address);
                            //storeRating.setText(Double.toString(rating) + "/ 5");
                        }
                    }

                    catch (JSONException e)
                    {
                        throw new RuntimeException(e);
                    }
                }

                taskFinishedCallBack();
            }
        });

        dataHandler.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                dataHandler.TYPE_GET_BY_STORE_ID, urlStr, storeId);
    }

    private void getStoreProducts() {
        // TODO: Add product status to xml layout.
        String urlStr = "/box/getProductByStoreId.php";
        productList = new ArrayList<>();

        DataHandler dataHandler = new DataHandler(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (!output.equals("NO"))
                {
                    Log.d("IN STORE DETAIL", "BEFORE SHOW STORE PRODUCT");
                    Log.d("IN STORE DETAIL", output);
                    try
                    {
                        JSONArray jsonArray = new JSONArray(output);

                        // Iterate over the JSON array
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String name = jsonObject.getString("tensanpham");
                            String image = jsonObject.getString("anhsanpham");
                            double price = jsonObject.getDouble("gia");

                            Product product = new Product(image, name, price);
                            productList.add(product);
                        }
                    }

                    catch (JSONException e)
                    {
                        throw new RuntimeException(e);
                    }
                }

                Log.d("TEST STORE", "AFTER SHOW STORE PRODUCT");
                taskFinishedCallBack();
            }
        });

        dataHandler.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                dataHandler.TYPE_GET_BY_STORE_ID, urlStr, storeId);
    }

    private void taskFinishedCallBack() {
        finishedTasks++;

        if (finishedTasks == totalTasks)
        {
            // All tasks are done
            // Set up recycler view
            setupRecyclerView();
        }
    }

    private void setupRecyclerView() {
        categoryRcv.setNestedScrollingEnabled(false);

        // Initialize category list
        List<Category<?>> categoryList = new ArrayList<>();

        // Category show the product
        Category<Product> products = new Category<Product>("", productList);
        categoryList.add(products);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1,
                GridLayoutManager.VERTICAL, false );
        categoryRcv.setLayoutManager(gridLayoutManager);

        // Set category to recyclerview
        CategoryAdapter categoryAdapter = new CategoryAdapter(this, categoryList);
        categoryAdapter.setType(ProductAdapter.PRODUCT_IN_STORE_TYPE);
        categoryRcv.setAdapter(categoryAdapter);
    }

    private void clickBack() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoreDetail.this, Home.class);
                startActivity(intent);
            }
        });
    }
}