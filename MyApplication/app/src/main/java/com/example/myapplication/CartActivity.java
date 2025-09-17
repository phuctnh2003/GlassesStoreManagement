package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView rvCartItems;
    private TextView tvTotalPrice;
    private Button btnCheckout;
    private Database database;
    private int userId;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rvCartItems = findViewById(R.id.rvCartItems);
        btnCheckout = findViewById(R.id.btnCheckout);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        database = new Database(this);

        // Lấy userId từ Intent
        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadCartItems();
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        btnCheckout.setOnClickListener(v -> {
            database.checkout(userId); // Thanh toán giỏ hàng
            Toast.makeText(this, "Pay Sucessfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, HomeActivity.class); // Quay lại trang chủ
            intent.putExtra("user_id", userId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void updateTotalPrice() {
        double totalPrice = 0;
        for (CartItem item : cartAdapter.getCartItems()) {
            totalPrice += item.getProductPrice() * item.getQuantity();
        }
        tvTotalPrice.setText(String.format("Total: %,.0f VND", totalPrice));
    }
    private void loadCartItems() {
        List<CartItem> cartItems = database.getCartItems(userId);
        if (cartItems.isEmpty()) {
            showEmptyCartDialog();
        } else {
            cartAdapter = new CartAdapter(cartItems, userId, database, this::updateTotalPrice);
            rvCartItems.setLayoutManager(new LinearLayoutManager(this));
            rvCartItems.setAdapter(cartAdapter);
            updateTotalPrice();
        }
    }

    private void showEmptyCartDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cart is empty")
                .setMessage("You don't have any products in your cart.")
                .setPositiveButton("OK", (dialog, which) -> finish()) // Quay lại trang chủ
                .setCancelable(false)
                .show();
    }
}