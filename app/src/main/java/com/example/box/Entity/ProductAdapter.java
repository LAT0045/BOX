package com.example.box.Entity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.box.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{

    private Context context;
    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    private List<Product> productList;

    public ProductAdapter(Context context,List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    public void setData(List<Product> list)
    {
        this.productList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        if(product == null){
            return;
        }

        Picasso.get().load(product.getProductImg()).into(holder.productImg);
        holder.productName.setText(product.getProductName());
        holder.productPrice.setText(Double.toString(product.getProductPrice()));
    }

    @Override
    public int getItemCount() {
        if(productList != null)
            return productList.size();
        return 0;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImg;
        private TextView productName;
        private TextView productPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productImg = itemView.findViewById(R.id.productImg);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
        }

        private void getProductInfo() {
            String urlStr = "/box/getProduct.php";

            JSONArray jsonArray = new JSONArray();

            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = null;
                try {
                    jsonObject = jsonArray.getJSONObject(i);

                    String productName = jsonObject.getString("tensanpham");
                    double productPrice  = jsonObject.getDouble("gia");
                    String productImg = jsonObject.getString("anhsanpham");

                    Product product = new Product(productImg, productName, productPrice);
                    productList.add(product);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }
}
