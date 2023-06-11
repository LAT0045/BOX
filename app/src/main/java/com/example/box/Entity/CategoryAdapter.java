package com.example.box.Entity;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.box.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Activity activity;
    private Context context;
    private List<?> categoryList;
    private String type;

    public interface CartChanged {
        void onCartChanged(Product product, boolean isAdded);
    }

    private CartChanged cartChanged;

    public CategoryAdapter(Activity activity, List<?> categoryList) {
        this.activity = activity;
        this.categoryList = categoryList;
    }

    public CategoryAdapter(Activity activity, List<?> categoryList, CartChanged cartChanged) {
        this.activity = activity;
        this.categoryList = categoryList;
        this.cartChanged = cartChanged;
    }

    public void setData(List<?> categoryList)
    {
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }

    public void setType(String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.product_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category<?> category = (Category<?>) categoryList.get(position);

        if(category == null)
            return;

        holder.categoryTV.setText(category.getCategoryName());

        holder.categoryRCV.setNestedScrollingEnabled(false);

        List<?> list = category.getList();

        if (list.get(0).getClass().equals(Product.class))
        {
            ProductAdapter productAdapter;
            // Set up layout manager for product
            if (type.equals(ProductAdapter.PRODUCT_IN_HOME_TYPE))
            {
                // Make text view gone if it's product category
                holder.categoryTV.setVisibility(View.GONE);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,
                        LinearLayoutManager.HORIZONTAL, false);
                holder.categoryRCV.setLayoutManager(linearLayoutManager);

                productAdapter = new ProductAdapter(activity, (List<Product>) category.getList(), type);
            }

            else
            {
                Log.d("CATEGORY ADPATER:", "HERE_________________");
                Product tmp = (Product) list.get(0);

                if (type.equals(ProductAdapter.PRODUCT_IN_STORE_TYPE))
                {
                    holder.categoryTV.setText(tmp.getSection());
                }

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,
                        LinearLayoutManager.VERTICAL, false);
                holder.categoryRCV.setLayoutManager(linearLayoutManager);

                // Add this line so the recycler view won't crash
                holder.categoryRCV.setItemAnimator(null);

                productAdapter = new ProductAdapter(activity, (List<Product>) category.getList(),
                        type, new ProductAdapter.AddProductToCartListener() {
                    @Override
                    public void onAddingToCartCallBack(Product product, boolean isAdded) {
                        cartChanged.onCartChanged(product, isAdded);
                    }
                });
            }

            // Set product adapter
            holder.categoryRCV.setAdapter(productAdapter);
        }

        else if (list.get(0).getClass().equals(Store.class))
        {
            // Set up layout manager for store
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL, false);
            holder.categoryRCV.setLayoutManager(linearLayoutManager);

            // Set store adapter
            StoreAdapter storeAdapter = new StoreAdapter(context, (List<Store>) category.getList());
            holder.categoryRCV.setAdapter(storeAdapter);
        }
    }

    @Override
    public int getItemCount() {
        if (categoryList != null)
            return categoryList.size();
        return 0;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView categoryTV;

        //Chứa product
        private RecyclerView categoryRCV;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryTV = itemView.findViewById(R.id.category_tv);
            categoryRCV = itemView.findViewById(R.id.category_product);
        }
    }
}
