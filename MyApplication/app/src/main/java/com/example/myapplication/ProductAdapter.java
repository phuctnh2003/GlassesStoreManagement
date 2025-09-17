package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> productList;
    private Database database;
    private int userId;

    public ProductAdapter(Context context, List<Product> productList, int userId) {
        this.productList = productList;
        this.database = new Database(context);
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format("%,.0f VND", product.getPrice()));

        int imageResId = holder.itemView.getContext()
                .getResources()
                .getIdentifier(product.getImage().replace(".jpg", ""), "drawable", holder.itemView.getContext().getPackageName());

        Glide.with(holder.itemView.getContext())
                .load(imageResId)
                .into(holder.productImage);
        //Glide.with(holder.itemView.getContext()).load(product.getImage()).into(holder.productImage);

        // Xử lý sự kiện thêm vào giỏ hàng
        holder.btnAddToCart.setOnClickListener(v -> {
            database.addToCart(userId, product.getId(), 1); // Thêm 1 sản phẩm vào giỏ hàng
            Toast.makeText(holder.itemView.getContext(), "Added to cart successfully", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice;
        Button btnAddToCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
