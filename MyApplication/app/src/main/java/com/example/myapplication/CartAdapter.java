package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartItem> cartItems;
    private int userId;
    private Database database;
    private Runnable onDataChangeListener;


    public CartAdapter(List<CartItem> cartItems, int userId, Database database, Runnable onDataChangeListener) {
        this.cartItems = cartItems;
        this.userId = userId;
        this.database = database;
        this.onDataChangeListener = onDataChangeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.cartProductName.setText(cartItem.getProductName());
        holder.cartProductPrice.setText(String.format("%,.0f VND", cartItem.getProductPrice()));
        holder.cartProductQuantity.setText(String.valueOf(cartItem.getQuantity()));
        int imageResId = holder.itemView.getContext()
                .getResources()
                .getIdentifier(cartItem.getProductImage().replace(".jpg", ""), "drawable", holder.itemView.getContext().getPackageName());

        if (imageResId != 0) {
            holder.cartProductImage.setImageResource(imageResId);
        } else {

            holder.cartProductImage.setImageResource(R.drawable.login_register_logo);
        }
        holder.btnIncrease.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() + 1;
            database.updateCartQuantity(userId, cartItem.getProductId(), newQuantity);
            cartItem.setQuantity(newQuantity);
            notifyDataSetChanged();
            onDataChangeListener.run();
        });
        // Xử lý giảm số lượng
        holder.btnDecrease.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() - 1;
            if (newQuantity > 0) {
                database.updateCartQuantity(userId, cartItem.getProductId(), newQuantity);
                cartItem.setQuantity(newQuantity);
                notifyDataSetChanged();
            } else {
                database.removeItemFromCart(userId, cartItem.getProductId());
                cartItems.remove(position);
                notifyDataSetChanged();
            }
            onDataChangeListener.run(); // Cập nhật tổng giá
        });

        // Xử lý xóa sản phẩm
        holder.btnRemoveItem.setOnClickListener(v -> {
            database.removeItemFromCart(userId, cartItem.getProductId());
            cartItems.remove(position);
            notifyDataSetChanged();
            onDataChangeListener.run();
            Toast.makeText(holder.itemView.getContext(), "Removed cart sucessfully", Toast.LENGTH_SHORT).show();
        });
    }
    public List<CartItem> getCartItems() {
        return cartItems;
    }
    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cartProductImage;
        TextView cartProductName, cartProductPrice, cartProductQuantity;
        ImageButton btnRemoveItem, btnIncrease, btnDecrease;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cartProductImage = itemView.findViewById(R.id.cartProductImage);
            cartProductName = itemView.findViewById(R.id.cartProductName);
            cartProductPrice = itemView.findViewById(R.id.cartProductPrice);
            cartProductQuantity = itemView.findViewById(R.id.cartProductQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnRemoveItem = itemView.findViewById(R.id.btnRemoveItem);
        }
    }
}