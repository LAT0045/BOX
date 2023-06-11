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

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{
    public static final String PRODUCT_IN_HOME_TYPE = "Home";
    public static final String PRODUCT_IN_STORE_TYPE = "Store";
    public static final String TOPPING = "Topping";
    public static final String CHECKOUT = "Checkout";

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
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.product_card, parent, false);
        }

        else if (type.equals(PRODUCT_IN_STORE_TYPE) || type.equals(TOPPING) || type.equals(CHECKOUT))
        {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.product_in_store_layout, parent, false);
        }

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, @SuppressLint("RecyclerView") int position) {
        this.position = position;
        Product product = productList.get(position);

        if(product == null){
            return;
        }

        if (type.equals(PRODUCT_IN_HOME_TYPE))
        {
            Picasso.get().load(product.getProductImg()).into(holder.productImage);
            holder.productName.setText(product.getProductName());
            holder.productPrice.setText(Integer.toString(product.getProductPrice()) + "Đ");

        }

        else if (type.equals(PRODUCT_IN_STORE_TYPE))
        {
            Picasso.get().load(product.getProductImg()).into(holder.productImage);

            holder.productName.setText(product.getProductName());

            holder.productPrice.setText(product.getProductPrice() + "Đ");

            holder.productQuantity.setText(Integer.toString(product.getCurQuantity()));

            if (product.getCurQuantity() > 0)
            {
                holder.removeButton.setVisibility(View.VISIBLE);
            }

            else
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
                    addProductToCartListener.onAddingToCartCallBack(product, false);
                }
            });
        }

        else if (type.equals(TOPPING))
        {
            Picasso.get().load(product.getProductImg()).into(holder.productImage);

            holder.productName.setText(product.getProductName());
            int price = product.getProductPrice();

            holder.productPrice.setText(Integer.toString((int) price) + "Đ");

            holder.productQuantity.setText(Integer.toString(product.getCurQuantity()));

            if (product.getCurQuantity() > 0)
            {
                holder.removeButton.setVisibility(View.VISIBLE);
            }

            else
            {
                holder.removeButton.setVisibility(View.INVISIBLE);
            }

            // Click on add button to add topping
            holder.addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int curQuantity = product.getCurQuantity();
                    curQuantity++;
                    product.setCurQuantity(curQuantity);
                    notifyItemChanged(position);
                    addProductToCartListener.onAddingToCartCallBack(product, true);
                }
            });

            // Click on remove button to remove topping
            holder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int curQuantity = product.getCurQuantity();
                    curQuantity--;
                    product.setCurQuantity(curQuantity);
                    notifyItemChanged(position);
                    addProductToCartListener.onAddingToCartCallBack(product, false);
                }
            });
        }

        else if (type.equals(CHECKOUT))
        {
            Picasso.get().load(product.getProductImg()).into(holder.productImage);

            holder.productName.setText(product.getProductName());

            holder.productPrice.setText(product.getProductPrice() + "Đ");

            holder.productQuantity.setText("x" + Integer.toString(product.getCurQuantity()));

            holder.addButton.setVisibility(View.INVISIBLE);
            holder.removeButton.setVisibility(View.INVISIBLE);

            List<Product> toppings = product.getToppingList();

            if (toppings.size() > 0)
            {
                StringBuilder stringBuilder = new StringBuilder();

                for (Product topping : toppings)
                {
                    stringBuilder.append(topping.getProductName());
                }

                holder.toppingList.setText(stringBuilder.toString());
            }
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
        private TextView toppingList;

        private ImageView addButton;
        private ImageView removeButton;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            toppingList = itemView.findViewById(R.id.topping_list);

            addButton = itemView.findViewById(R.id.add_button);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }
}
