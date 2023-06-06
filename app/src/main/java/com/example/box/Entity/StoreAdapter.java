package com.example.box.Entity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.box.FragmentAndActivity.StoreDetail;
import com.example.box.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {
    private Context context;
    private List<Store> storeList;

    public StoreAdapter(Context context, List<Store> storeList) {
        this.context = context;
        this.storeList = storeList;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_layout, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        Store store = storeList.get(position);

        if (store == null)
        {
            return;
        }

        Picasso.get().load(store.getStoreImage()).into(holder.storeImage);
        holder.storeName.setText(store.getStoreName());
        holder.storeAddress.setText(store.getStoreAddress());

        Log.d("IN STORE ADAPTER", "BEFORE CLICK");
        // Show store detail when click on store
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("IN STORE ADAPTER", "START CLICK");
                Intent intent = new Intent(context, StoreDetail.class);
                intent.putExtra("storeId", store.getStoreId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (storeList != null)
        {
            return storeList.size();
        }

        return 0;
    }

    public class StoreViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView storeImage;
        private TextView storeName;
        private TextView storeAddress;

        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);

            storeImage = itemView.findViewById(R.id.store_image);
            storeName = itemView.findViewById(R.id.store_name);
            storeAddress = itemView.findViewById(R.id.store_address);
        }
    }
}