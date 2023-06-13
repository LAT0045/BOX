package com.example.box.Entity;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.box.FragmentAndActivity.ActivityPresentOrder;
import com.example.box.FragmentAndActivity.Order;
import com.example.box.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PurchaseOrderAdapter extends RecyclerView.Adapter<PurchaseOrderAdapter.PurchaseOrderViewHolder>{

    List<PurchaseOrder> purchaseOrderList;
    Context context;

    public PurchaseOrderAdapter(List<PurchaseOrder> purchaseOrderList)
    {
        this.purchaseOrderList = purchaseOrderList;
    }

    @NonNull
    @Override
    public PurchaseOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.present_order_card, parent, false);
        context = parent.getContext();
        return new PurchaseOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseOrderViewHolder holder, int position) {
        PurchaseOrder purchaseOrder = purchaseOrderList.get(position);

        if(purchaseOrder == null)
        {
            return;
        }

        holder.date.setText(purchaseOrder.getDate());
        holder.status.setText(purchaseOrder.getStatus());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ActivityPresentOrder.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(purchaseOrderList != null)
        {
            return purchaseOrderList.size();
        }
        return 0;
    }

    public class PurchaseOrderViewHolder extends RecyclerView.ViewHolder{

        private TextView date;
        private TextView status;


        public PurchaseOrderViewHolder(@NonNull View itemView) {
            super(itemView);

            date = (TextView) itemView.findViewById(R.id.ngay_dat);
            status = (TextView) itemView.findViewById(R.id.tinh_trang);

        }
    }
}
