package com.example.box.Entity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.box.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductDetail {
    private View view;
    private Activity activity;
    private Product product;
    private AlertDialog alertDialog;

    private ShapeableImageView productImage;
    private TextView productName;
    private TextView productPrice;
    private TextView productDescription;

    private RecyclerView toppingRcv;
    private List<Product> toppingList;
    private List<Product> curToppingList = new ArrayList<>();

    private AppCompatEditText noteEditText;

    private RelativeLayout addButton;
    private ImageView closeButton;

    public interface AddProductListener {
        void onAddingProductCallBack(Product curProduct);
    }

    private AddProductListener addProductListener;

    public ProductDetail(Activity activity, Product product,
                         AddProductListener addProductListener) {
        this.activity = activity;
        this.product = product;
        this.addProductListener = addProductListener;
    }

    public void showProductDetail() {
        // Inflate view
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.product_detail_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.productAnimation;
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);

        // Initialize UI
        initializeUI();

        showInfo();

        getTopping();

        clickAdd();

        closeProductionInfo();
    }

    private void closeProductionInfo() {
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void initializeUI() {
        productImage = view.findViewById(R.id.product_detail_image);
        productName = view.findViewById(R.id.product_detail_name);
        productPrice = view.findViewById(R.id.product_detail_price);
        productDescription = view.findViewById(R.id.product_detail_description);

        toppingRcv = view.findViewById(R.id.topping_rcv);
        noteEditText = view.findViewById(R.id.note_edit_text);

        addButton = view.findViewById(R.id.add_button);
        closeButton = view.findViewById(R.id.close_button);
    }

    private void showInfo() {
        Picasso.get()
                .load(product.getProductImg())
                .into(productImage);

        productName.setText(product.getProductName());

        double price = product.getProductPrice();
        if (price != Math.round(price))
        {
            DecimalFormat decimalFormat = new DecimalFormat("#.###");
            decimalFormat.setRoundingMode(RoundingMode.DOWN);

            String priceStr = decimalFormat.format(price);
            price = Double.parseDouble(priceStr);
            price *= 1000;
        }

        productPrice.setText(Integer.toString((int) price) + "Đ");

        if (product.getDescription().equals("null"))
        {
            productDescription.setText("Không có mô tả nào cho sản phẩm này.");
        }

        else
        {
            productDescription.setText(product.getDescription());
        }
    }

    private void getTopping() {
        String urlStr = "/box/getTopping.php";

        DataHandler dataHandler = new DataHandler(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                Log.d("PRODUCT DETAIL TOPPING", output);
                if (!output.equals("NO") && !output.equals("[]"))
                {
                    toppingList = new ArrayList<>();

                    try
                    {
                        JSONArray jsonArray = new JSONArray(output);

                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String productId = jsonObject.getString("masanpham");
                            String productName = jsonObject.getString("tensanpham");
                            double productPrice  = jsonObject.getDouble("gia");
                            String productImg = jsonObject.getString("anhsanpham");
                            String section = jsonObject.getString("phanloai");
                            String description = jsonObject.getString("mota");
                            String storeId = jsonObject.getString("cuahang_id");

                            if (productPrice != Math.round(productPrice))
                            {
                                DecimalFormat decimalFormat = new DecimalFormat("#.###");
                                decimalFormat.setRoundingMode(RoundingMode.DOWN);

                                String priceStr = decimalFormat.format(productPrice);
                                productPrice = Double.parseDouble(priceStr);
                                productPrice *= 1000;
                            }

                            Product product = new Product(productId, productImg, productName,
                                    (int) productPrice, section, description, storeId);
                            toppingList.add(product);
                        }

                        setUpRecyclerView();
                    }

                    catch (JSONException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        dataHandler.execute(dataHandler.TYPE_GET_TOPPING, urlStr, product.getStoreId());
    }

    private void setUpRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity, 1,
                GridLayoutManager.VERTICAL, false );
        toppingRcv.setLayoutManager(gridLayoutManager);

        List<Category<?>> categoryList = new ArrayList<Category<?>>();
        Category<Product> toppings = new Category<Product>("", toppingList);
        categoryList.add(toppings);

        CategoryAdapter categoryAdapter = new CategoryAdapter(activity, categoryList,
                new CategoryAdapter.CartChanged() {
                    @Override
                    public void onCartChanged(Product product, boolean isAdded) {
                        int position = getExistedToppingPosition(product.getProductId());
                        Log.d("PRODUCT DETAIL POSITION", Integer.toString(position));

                        if (isAdded)
                        {
                            if (position == -1)
                            {
                                curToppingList.add(product);
                            }

                            else
                            {
                                // User already added this product
                                // Update the quantity
                                int quantity = curToppingList.get(position).getCurQuantity();
                                curToppingList.get(position).setCurQuantity(quantity);
                            }
                        }

                        else
                        {
                            // Update the quantity when user remove a product
                            int quantity = curToppingList.get(position).getCurQuantity();
                            curToppingList.get(position).setCurQuantity(quantity);

                            // If quantity is 0 then remove the product
                            if (quantity == 0)
                            {
                                curToppingList.remove(position);
                            }
                        }
                    }
                });
        categoryAdapter.setType(ProductAdapter.TOPPING);
        toppingRcv.setAdapter(categoryAdapter);
    }

    private int getExistedToppingPosition(String productId) {
        int res = -1;

        if (curToppingList != null && curToppingList.size() != 0)
        {
            for(int i = 0; i < curToppingList.size(); i++)
            {
                if (curToppingList.get(i).getProductId().equals(productId))
                {
                    res = i;
                    break;
                }
            }
        }

        return res;
    }

    private void clickAdd() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addProductListener != null)
                {
                    product.setCustomerNote(noteEditText.getText().toString().trim());
                    int curQuantity = product.getCurQuantity();
                    curQuantity++;
                    product.setCurQuantity(curQuantity);
                    product.setToppingList(curToppingList);
                    addProductListener.onAddingProductCallBack(product);
                }
                alertDialog.dismiss();
            }
        });
    }
}
