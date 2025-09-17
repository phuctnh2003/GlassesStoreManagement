package com.example.myapplication;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView rvPopularProducts;
    private RecyclerView rvCategories;
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private Database database;
    private EditText searchBar;
    private Spinner spinnerFilter;
    private ImageButton cartButton, profileButton, homeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        database = new Database(this);
        searchBar = findViewById(R.id.searchBar);
        spinnerFilter = findViewById(R.id.spinnerFilter);
        rvPopularProducts = findViewById(R.id.rvPopularProducts);
        rvCategories = findViewById(R.id.rvCategories);
        cartButton = findViewById(R.id.cartButton);
        homeButton = findViewById(R.id.homeButton);
        profileButton = findViewById(R.id.accountButton);
        //database.addSampleGlasses();
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        loadCategories();

        rvPopularProducts.setLayoutManager(new LinearLayoutManager(this));
        loadProducts(null);


        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        String[] filterOptions = {"Sort By","Price Low to High", "Price High to Low"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filterOptions);
        spinnerFilter.setAdapter(adapter);


        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    loadProductsSortedByPrice(true); // Giá tăng dần
                } else if (position == 2) {
                    loadProductsSortedByPrice(false); // Giá giảm dần
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userId = getIntent().getIntExtra("user_id", -1);
                if (userId != -1) {
                    Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                    intent.putExtra("user_id", userId);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "Not identify id user", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cartButton.setOnClickListener(v -> {
            int userId = getIntent().getIntExtra("user_id", -1);
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userId = getIntent().getIntExtra("user_id", -1);
                if (userId != -1) {
                    Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                    intent.putExtra("user_id", userId);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "Not identify id user", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    // Load danh sách sản phẩm theo từ khóa tìm kiếm
    private void loadProducts(String keyword) {
        int userId = getIntent().getIntExtra("user_id", -1);
        List<Product> productList = database.searchProducts(keyword);
        productAdapter = new ProductAdapter(this,productList,userId);
        rvPopularProducts.setAdapter(productAdapter);
    }

    // Danh mục
    private void loadCategories() {
        List<String> categoriesList = new ArrayList<>();
        categoriesList.add("All"); // Thêm mục "All" vào danh sách

        categoriesList.addAll(database.getAllCategories()); // Thêm các danh mục từ database

        categoryAdapter = new CategoryAdapter(categoriesList, this, new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(String category) {
                if (category.equals("All")) {
                    loadProducts(null); // Hiển thị lại tất cả sản phẩm
                } else {
                    loadProductsByCategory(category);
                }
            }
        });
        rvCategories.setAdapter(categoryAdapter);
    }


    private void loadProductsSortedByPrice(boolean ascending) {
        int userId = getIntent().getIntExtra("user_id", -1);
        List<Product> productList = database.getProductsSortedByPrice(ascending);
        productAdapter = new ProductAdapter(this,productList,userId);
        rvPopularProducts.setAdapter(productAdapter);
    }
    private void loadProductsByCategory(String category) {
        int userId = getIntent().getIntExtra("user_id", -1);
        List<Product> productList = database.getProductsByCategory(category);
        productAdapter = new ProductAdapter(this,productList,userId);
        rvPopularProducts.setAdapter(productAdapter);
    }
}
