package com.example.box.FragmentAndActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreDetail extends AppCompatActivity {
    private String storeId;

    private ImageView storeImage;
    private TextView storeName;
    private TextView storeAddress;
    private TextView time;
    private TextView storeRating;

    private RecyclerView categoryRcv;
    private List<Product> productList;
    private Map<String, List<Product>> map;

    private TextView quantityText;
    private TextView priceText;
    private int curQuantity = 0;
    private int curPrice = 0;

    private ImageView backButton;
    private RelativeLayout checkOutButton;

    private int totalTasks = 2;
    private int finishedTasks = 0;

    // This list stores all items in carts
    private List<Product> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        // Get the current store's id
        storeId = getIntent().getStringExtra("storeId");

        // Initialize UI
        initializeUI();

        quantityText.setText(Integer.toString(curQuantity) + " sản phẩm");
        priceText.setText(Integer.toString(curPrice) + "Đ");

        getStoreById();

        getStoreProducts();

        clickCheckOut();

        clickBack();
    }

    private void initializeUI() {
        storeImage = findViewById(R.id.store_image);
        storeName = findViewById(R.id.store_name);
        storeAddress = findViewById(R.id.store_address);
        time = findViewById(R.id.time);
        storeRating = findViewById(R.id.store_rating);

        categoryRcv = findViewById(R.id.category_rcv);

        quantityText = findViewById(R.id.quantity_text);
        priceText = findViewById(R.id.price_text);

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
                            double rating = jsonObject.getDouble("chatluong");
                            String openingTime = jsonObject.getString("giomocua");
                            String closingTime = jsonObject.getString("giodongcua");

                            Picasso.get()
                                    .load(image)
                                    .into(storeImage);

                            storeName.setText(name);
                            storeAddress.setText(address);
                            storeRating.setText(Double.toString(rating).trim() + " / 5");

                            openingTime = openingTime.substring(0, openingTime.length() - 3);
                            closingTime = closingTime.substring(0, closingTime.length() - 3);
                            time.setText(openingTime + " - " + closingTime);
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

        DataHandler dataHandler = new DataHandler(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (!output.equals("NO"))
                {
                    try
                    {
                        JSONArray jsonArray = new JSONArray(output);

                        // Create a hash map to store products
                        // Key is the section of that product
                        map = new HashMap<>();

                        // Iterate over the JSON array
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String productId = jsonObject.getString("masanpham");
                            String name = jsonObject.getString("tensanpham");
                            String image = jsonObject.getString("anhsanpham");
                            double price = jsonObject.getDouble("gia");
                            String section = jsonObject.getString("phanloai");
                            String description = jsonObject.getString("mota");
                            String storeId = jsonObject.getString("cuahang_id");

                            if (price != Math.round(price))
                            {
                                DecimalFormat decimalFormat = new DecimalFormat("#.###");
                                decimalFormat.setRoundingMode(RoundingMode.DOWN);

                                String priceStr = decimalFormat.format(price);
                                price = Double.parseDouble(priceStr);
                                price *= 1000;
                            }

                            Product product = new Product(productId, image, name,
                                    (int) price, section, description, storeId);

                            // Create a tmp product list to store the current list get from map by key
                            List<Product> tmpProductList = new ArrayList<>();

                            // Get the list from map with key as the current section
                            tmpProductList = map.get(section);

                            // If key doesn't exist then tmp list is null
                            // Initialize again if tmp list is null
                            if (tmpProductList == null)
                            {
                                tmpProductList = new ArrayList<>();
                            }

                            // And add the current product to that list
                            tmpProductList.add(product);

                            // Update the map value
                            map.put(section, tmpProductList);
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

        // Initialize category and product list
        List<Category<?>> categoryList = new ArrayList<Category<?>>();

        for(String key : map.keySet())
        {
            productList = new ArrayList<>();
            productList = map.get(key);

            Category<Product> products = new Category<Product>("", productList);
            categoryList.add(products);
        }

        Collections.reverse(categoryList);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1,
                GridLayoutManager.VERTICAL, false );
        categoryRcv.setLayoutManager(gridLayoutManager);

        // Restore cart items if user return from check out
        cartItems = getIntent().getParcelableArrayListExtra("checkOutList");
        if (cartItems != null)
        {
            categoryList = restoreCartItem(categoryList);

            // Update price
            priceText.setText(Integer.toString(getTotalPrice()) + "Đ");

            // Update quantity
            quantityText.setText(Integer.toString(getAllItemsInCart()) + " sản phẩm");
        }

        else
        {
            cartItems = new ArrayList<>();
        }

        // Set category to recyclerview
        CategoryAdapter categoryAdapter = new CategoryAdapter(this,
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
                        cartItems.add(product);
                    }

                    else
                    {
                        // User already added this product
                        // Update the quantity
                        int quantity = cartItems.get(position).getCurQuantity();
                        cartItems.get(position).setCurQuantity(quantity);
                    }
                }

                else
                {
                    // Update the quantity when user remove a product
                    int quantity = cartItems.get(position).getCurQuantity();
                    cartItems.get(position).setCurQuantity(quantity);

                    // If quantity is 0 then remove the product
                    if (quantity == 0)
                    {
                        cartItems.remove(position);
                    }
                }

                // Update quantity text
                quantityText.setText(Integer.toString(getAllItemsInCart()) + " sản phẩm");

                // Update price text
                priceText.setText(Integer.toString(getTotalPrice()) + "Đ");

                if (cartItems != null)
                {
                    for(int i = 0; i < cartItems.size(); i++)
                    {
                        Log.d("THIS IS THE ITEM IN CART (BEFORE):", "THIS IS THE ITEM IN CART (BEFORE):");
                        Log.d("THIS IS THE ITEM IN CART (BEFORE):", cartItems.get(i).getProductName()
                                + "_" + product.getProductId());
                    }

                    Log.d("", "______________________________");
                }
            }
        });
        categoryAdapter.setType(ProductAdapter.PRODUCT_IN_STORE_TYPE);
        categoryRcv.setAdapter(categoryAdapter);
    }

    private List<Category<?>> restoreCartItem(List<Category<?>> categoryList) {
        Map<String, Product> map = new HashMap<>();
        for(Product product : cartItems)
        {
            map.put(product.getProductId(), product);
        }

        for(int i = 0; i < categoryList.size(); i++)
        {
            boolean isChanged = false;
            List<Product> tmpProducts = (List<Product>) categoryList.get(i).getList();

            for(int j = 0; j < tmpProducts.size(); j++)
            {
                Product tmpProduct = map.get(tmpProducts.get(j).getProductId());

                if (tmpProduct != null)
                {
                    tmpProducts.get(j).setCurQuantity(tmpProduct.getCurQuantity());
                    isChanged = true;
                }
            }

            if (isChanged)
            {
                categoryList.get(i).setList(tmpProducts);
            }
        }

        return categoryList;
    }

    private int getExistedProductPosition(String productId) {
        int res = -1;

        if (cartItems != null && cartItems.size() != 0)
        {
            for(int i = 0; i < cartItems.size(); i++)
            {
                if (cartItems.get(i).getProductId().equals(productId))
                {
                    res = i;
                    break;
                }
            }
        }

        return res;
    }

    private int getAllItemsInCart() {
        int total = 0;

        if (cartItems != null)
        {
            for(Product product : cartItems)
            {
                total += product.getCurQuantity();
            }
        }

        return total;
    }

    private int getTotalPrice() {
        int total = 0;
        for(Product product : cartItems)
        {
            total += (product.getProductPrice() * product.getCurQuantity());
        }
        return total;
    }

    private void clickCheckOut() {
        checkOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoreDetail.this, Order.class);
                intent.putParcelableArrayListExtra("checkOutList",
                        (ArrayList<? extends Parcelable>) cartItems);
                intent.putExtra("storeId", storeId);
                startActivity(intent);
            }
        });
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