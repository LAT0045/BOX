package com.example.box.Entity;

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

import java.lang.reflect.Type;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context context;
    private List<?> categoryList;
    private String type;

    public CategoryAdapter(Context context, List<?> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    public void setData(List<Type> List)
    {
        this.categoryList = List;
        notifyDataSetChanged();
    }

    public void setType(String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_category, parent, false);
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
            Log.d("IN CATEGORY ADAPTER", type);
            // Set up layout manager for product
            if (type.equals(ProductAdapter.PRODUCT_IN_HOME_TYPE))
            {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                holder.categoryRCV.setLayoutManager(linearLayoutManager);
            }

            else
            {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                holder.categoryRCV.setLayoutManager(linearLayoutManager);
            }

            // Make text view gone if it's product category
            holder.categoryTV.setVisibility(View.GONE);

            // Set product adapter
            ProductAdapter productAdapter = new ProductAdapter(context, (List<Product>) category.getList(), type);
            holder.categoryRCV.setAdapter(productAdapter);
        }

        else if (list.get(0).getClass().equals(Store.class))
        {
            // Set up layout manager for store
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
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
