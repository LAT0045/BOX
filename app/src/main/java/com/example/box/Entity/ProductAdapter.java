package com.example.box.Entity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.box.R;
import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{
    public static final String PRODUCT_IN_HOME_TYPE = "Home";
    public static final String PRODUCT_IN_STORE_TYPE = "Store";

    private View view;
    private Context context;
    private List<Product> productList;
    private String type;
    private int quantity = 0;
    private RecyclerView.ViewHolder holder;
    private int position;

    public ProductAdapter(Context context, List<Product> productList, String type) {
        this.context = context;
        this.productList = productList;
        this.type = type;
    }

    public void setData(List<Product> list)
    {
        this.productList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (type.equals(PRODUCT_IN_HOME_TYPE))
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
        }

        else if (type.equals(PRODUCT_IN_STORE_TYPE))
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_in_store_layout, parent, false);
        }

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, @SuppressLint("RecyclerView") int position) {
        this.position = position;
        Product product = productList.get(position);
        Log.d("TYPEEEEEE", type);

        if (type.equals(PRODUCT_IN_HOME_TYPE))
        {
            if(product == null){
                return;
            }

            Picasso.get().load(product.getProductImg()).into(holder.productImage);
            holder.productName.setText(product.getProductName());
            double price = product.getProductPrice();

            if (price != Math.round(price))
            {
                DecimalFormat decimalFormat = new DecimalFormat("#.###");
                decimalFormat.setRoundingMode(RoundingMode.DOWN);

                String priceStr = decimalFormat.format(price);
                price = Double.parseDouble(priceStr);
                price *= 1000;
            }

            holder.productPrice.setText(Integer.toString((int) price) + "Đ");

        }

        else if (type.equals(PRODUCT_IN_STORE_TYPE))
        {
            ProductViewHolder productViewHolder = (ProductViewHolder) holder;

            Picasso.get().load(product.getProductImg()).into(productViewHolder.productImage);
            productViewHolder.productName.setText(product.getProductName());
            double price = product.getProductPrice();

            if (price != Math.round(price))
            {
                DecimalFormat decimalFormat = new DecimalFormat("#.###");
                decimalFormat.setRoundingMode(RoundingMode.DOWN);

                String priceStr = decimalFormat.format(price);
                price = Double.parseDouble(priceStr);
                price *= 1000;
            }

            productViewHolder.productPrice.setText(Integer.toString((int) price) + "Đ");
            productViewHolder.productQuantity.setText(Integer.toString(quantity));

            // TODO: Clean Code
            // TODO: Fix quantity
            // User add a product
            productViewHolder.addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (quantity == 0)
                    {
                        productViewHolder.removeButton.setVisibility(View.VISIBLE);
                    }

                    quantity++;
                    productViewHolder.productQuantity.setText(Integer.toString(quantity));
                }
            });

            // User remove a product
            productViewHolder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (quantity == 1)
                    {
                        productViewHolder.removeButton.setVisibility(View.GONE);
                    }

                    quantity--;
                    productViewHolder.productQuantity.setText(Integer.toString(quantity));
                }
            });

            Log.d("TYPEEEEEEE", "OKAYYYYYYYYY");
        }
    }

    @Override
    public int getItemCount() {
        if(productList != null)
            return productList.size();
        return 0;
    }

    // This view holder is for product in home display
    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productName;
        private TextView productPrice;

        private TextView productQuantity;

        private ImageView addButton;
        private ImageView removeButton;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);

            productQuantity = itemView.findViewById(R.id.product_quantity);

            addButton = itemView.findViewById(R.id.add_button);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }

    // This view holder is for product in store display
    public class ProductStoreViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productName;
        private TextView productPrice;
        private TextView productQuantity;

        private ImageView addButton;
        private ImageView removeButton;

        public ProductStoreViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productQuantity = itemView.findViewById(R.id.product_quantity);

            addButton = itemView.findViewById(R.id.add_button);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }
}
