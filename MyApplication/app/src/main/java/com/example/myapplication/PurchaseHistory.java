package com.example.myapplication;

public class PurchaseHistory {
    private int productId;
    private String productName;
    private double productPrice;
    private String productImage;
    private int quantity;
    private String purchaseDate;

    public PurchaseHistory(int productId, String productName, double productPrice, String productImage, int quantity, String purchaseDate) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImage = productImage;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
    }

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }
}