package com.example.box.Entity;

import android.annotation.SuppressLint;
import android.app.Activity;
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
    private Activity activity;
    private List<Product> productList;
    private String type;
    private int position;

    public interface AddProductToCartListener {
        void onAddingToCartCallBack(Product product, boolean isAdded);
    }

    private AddProductToCartListener addProductToCartListener;

    public ProductAdapter(Activity activity, List<Product> productList, String type,
                          AddProductToCartListener addProductToCartListener) {
        this.productList = productList;
        this.type = type;
        this.activity = activity;
        this.addProductToCartListener = addProductToCartListener;
    }

    public ProductAdapter(Activity activity, List<Product> productList, String type) {
        this.productList = productList;
        this.type = type;
        this.activity = activity;
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

            holder.productQuantity.setText(Integer.toString(product.getCurQuantity()));

            if (product.getCurQuantity() == 0)
            {
                holder.removeButton.setVisibility(View.INVISIBLE);
            }

            // Show detail when click on product
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProductDetail productDetail = new ProductDetail(activity, product,
                            new ProductDetail.AddProductListener() {
                        @Override
                        public void onAddingProductCallBack(Product curProduct) {
                            product.setCustomerNote(curProduct.getCustomerNote());
                            product.setCurQuantity(curProduct.getCurQuantity());
                            notifyItemChanged(position);

                            holder.removeButton.setVisibility(View.VISIBLE);

                            addProductToCartListener.onAddingToCartCallBack(product, true);
                        }
                    });
                    productDetail.showProductDetail();
                }
            });

            // Click on add button to add more product
            holder.addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int curQuantity = product.getCurQuantity();
                    curQuantity++;
                    product.setCurQuantity(curQuantity);
                    notifyItemChanged(position);

                    if (curQuantity > 0)
                    {
                        holder.removeButton.setVisibility(View.VISIBLE);
                    }

                    addProductToCartListener.onAddingToCartCallBack(product, true);
                }
            });

            // Click on remove button to remove a product
            holder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int curQuantity = product.getCurQuantity();
                    curQuantity--;
                    product.setCurQuantity(curQuantity);
                    notifyItemChanged(position);

                    if (curQuantity == 0)
                    {
                        holder.removeButton.setVisibility(View.INVISIBLE);
                    }

                    addProductToCartListener.onAddingToCartCallBack(product, false);
                }
            });

            // TODO: Clean Code
            // TODO: Fix quantity
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
}
