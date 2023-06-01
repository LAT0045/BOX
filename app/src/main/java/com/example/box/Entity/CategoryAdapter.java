package com.example.box.Entity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.box.FragmentAndActivity.HomeFragment;
import com.example.box.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context context;
    private List<Category> categoryList;

    private List<Product> productList;

    public CategoryAdapter(Context context, List<Product> list, List<Category> categoryList) {
        this.context = context;
        this.productList = list;
        this.categoryList = categoryList;
    }

    public void setData(List<Category> List)
    {
        this.categoryList = List;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        if(category == null)
            return;

        holder.categoryTV.setText(category.getCategoryName());

        holder.categoryRCV.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.categoryRCV.setLayoutManager(linearLayoutManager);

        ProductAdapter productAdapter = new ProductAdapter(context, productList);
//        productAdapter.setData(category.getProductList());
        holder.categoryRCV.setAdapter(productAdapter);
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
