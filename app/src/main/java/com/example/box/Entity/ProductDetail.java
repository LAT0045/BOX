package com.example.box.Entity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;

import com.example.box.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class ProductDetail {
    private View view;
    private Activity activity;
    private Product product;
    private AlertDialog alertDialog;

    private ShapeableImageView productImage;
    private TextView productName;
    private TextView productPrice;
    private TextView productDescription;

    private RecyclerView sideDishRcv;

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

        sideDishRcv = view.findViewById(R.id.side_dish_rcv);
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
                    addProductListener.onAddingProductCallBack(product);
                }
                alertDialog.dismiss();
            }
        });
    }
}
