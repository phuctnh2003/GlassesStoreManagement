package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "UserDB";
    private static final int DATABASE_VERSION = 5;

    // Bảng users
    private static final String TABLE_USERS = "users";
    private static final String TABLE_PRODUCTS = "products";
    private static final String TABLE_CART = "cart";
    private static final String TABLE_HISTORY = "purchase_history";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Database", "Creating tables...");

        String createUsersTable = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, full_name TEXT, username TEXT UNIQUE, phone TEXT, gender TEXT, password TEXT)";

        String createProductsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCTS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, category TEXT, price REAL, image TEXT, stock INTEGER)";

        String createCartTable = "CREATE TABLE IF NOT EXISTS " + TABLE_CART + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "product_id INTEGER, " +
                "quantity INTEGER, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE)";

        String createPurchaseHistoryTable = "CREATE TABLE IF NOT EXISTS " + TABLE_HISTORY + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "product_id INTEGER, " +
                "quantity INTEGER, " +
                "purchase_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE)";

        db.execSQL(createUsersTable);
        db.execSQL(createProductsTable);
        db.execSQL(createCartTable);
        db.execSQL(createPurchaseHistoryTable);
        Log.d("Database", "Tables created successfully.");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);  // Tạo lại database với bảng mới
    }

    // Thêm user
    public boolean addUser(String fullName, String username, String phone, String gender, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", fullName);
        values.put("username", username);
        values.put("phone", phone);
        values.put("gender", gender);
        values.put("password", password);
        return db.insert(TABLE_USERS, null, values) != -1;
    }

    // Kiểm tra username tồn tại chưa
    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + TABLE_USERS + " WHERE username = ?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }



    public int getUserId(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE username = ? AND password = ?",
                new String[]{username, password});
        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            cursor.close();
            return userId;
        }
        cursor.close();
        return -1; // Không tìm thấy user
    }

    // Lấy thông tin người dùng từ ID
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE id = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getInt(0));
            user.setFullName(cursor.getString(1));
            user.setUsername(cursor.getString(2));
            user.setPhone(cursor.getString(3));
            user.setGender(cursor.getString(4));
            user.setPassword(cursor.getString(5));
            cursor.close();
            return user;
        }
        cursor.close();
        return null;
    }

    public boolean updatePassword(int userId, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        int rowsAffected = db.update(TABLE_USERS, values, "id = ?", new String[]{String.valueOf(userId)});

        // Trả về true nếu cập nhật thành công (ít nhất 1 dòng bị ảnh hưởng)
        return rowsAffected > 0;
    }


    // Thêm sản phẩm
    public long addProduct(String name, String category, double price, String image, int stock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("category", category);
        values.put("price", price);
        values.put("image", image);
        values.put("stock", stock);
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    // Đếm số sản phẩm
    public int getProductCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PRODUCTS, null);
        int count = cursor.moveToFirst() ? cursor.getInt(0) : 0;
        cursor.close();
        return count;
    }

    // Lấy danh sách sản phẩm
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);

        while (cursor.moveToNext()) {
            productList.add(new Product(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getString(4),
                    cursor.getInt(5)
            ));
        }
        cursor.close();
        return productList;
    }

    // Lấy danh mục sản phẩm
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT category FROM " + TABLE_PRODUCTS, null);

        while (cursor.moveToNext()) {
            categories.add(cursor.getString(0));
        }
        cursor.close();
        return categories;
    }

    // Tìm kiếm sản phẩm theo từ khóa hoặc thể loại
    public List<Product> searchProducts(String keyword) {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        if (keyword == null || keyword.isEmpty()) {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);
        } else {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS +
                            " WHERE LOWER(name) LIKE ? OR LOWER(category) LIKE ?",
                    new String[]{"%" + keyword.toLowerCase() + "%", "%" + keyword.toLowerCase() + "%"});
        }

        while (cursor.moveToNext()) {
            productList.add(new Product(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getString(4),
                    cursor.getInt(5)
            ));
        }
        cursor.close();
        return productList;
    }

    // Lấy danh sách sản phẩm theo giá (tăng dần hoặc giảm dần)
    public List<Product> getProductsSortedByPrice(boolean ascending) {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String orderBy = ascending ? "ASC" : "DESC";
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " ORDER BY price " + orderBy, null);

        while (cursor.moveToNext()) {
            productList.add(new Product(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getString(4),
                    cursor.getInt(5)
            ));
        }
        cursor.close();
        return productList;
    }
    // Loc theo the loai
    public List<Product> getProductsByCategory(String category) {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE category=?", new String[]{category});

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product(
                        cursor.getInt(0),  // id
                        cursor.getString(1),  // name
                        cursor.getString(2),  // category
                        cursor.getDouble(3),  // price
                        cursor.getString(4),  // image
                        cursor.getInt(5)  // stock
                );
                productList.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return productList;
    }

    public boolean isCartEmpty(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_CART + " WHERE user_id=?", new String[]{String.valueOf(userId)});
        boolean isEmpty = true;
        if (cursor.moveToFirst()) {
            isEmpty = cursor.getInt(0) == 0;
        }
        cursor.close();
        return isEmpty;
    }
    public void addToCart(int userId, int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT quantity FROM " + TABLE_CART + " WHERE user_id=? AND product_id=?",
                new String[]{String.valueOf(userId), String.valueOf(productId)});

        if (cursor.moveToFirst()) {
            int existingQuantity = cursor.getInt(0);
            ContentValues values = new ContentValues();
            values.put("quantity", existingQuantity + quantity);
            db.update(TABLE_CART, values, "user_id=? AND product_id=?", new String[]{String.valueOf(userId), String.valueOf(productId)});
        } else {
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("product_id", productId);
            values.put("quantity", quantity);
            db.insert(TABLE_CART, null, values);
        }
        cursor.close();
    }
    public List<CartItem> getCartItems(int userId) {
        List<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT c.product_id, p.name, p.price, p.image, c.quantity " +
                        "FROM " + TABLE_CART + " c JOIN " + TABLE_PRODUCTS + " p " +
                        "ON c.product_id = p.id WHERE c.user_id=?",
                new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            CartItem cartItem = new CartItem(
                    cursor.getInt(0),  // product_id
                    cursor.getString(1),  // name
                    cursor.getDouble(2),  // price
                    cursor.getString(3),  // image
                    cursor.getInt(4)  // quantity
            );
            cartItems.add(cartItem);
        }
        cursor.close();
        return cartItems;
    }
    public void updateCartQuantity(int userId, int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (quantity > 0) {
            ContentValues values = new ContentValues();
            values.put("quantity", quantity);
            db.update(TABLE_CART, values, "user_id=? AND product_id=?", new String[]{String.valueOf(userId), String.valueOf(productId)});
        } else {
            removeItemFromCart(userId, productId);
        }
    }
    public void removeItemFromCart(int userId, int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, "user_id=? AND product_id=?", new String[]{String.valueOf(userId), String.valueOf(productId)});
    }
    public void clearCart(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, "user_id=?", new String[]{String.valueOf(userId)});
    }

    public void addPurchaseHistory(int userId, int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("product_id", productId);
        values.put("quantity", quantity);
        db.insert("purchase_history", null, values);
    }

    public List<PurchaseHistory> getPurchaseHistory(int userId) {
        List<PurchaseHistory> purchaseHistoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ph.product_id, p.name, p.price, p.image, ph.quantity, ph.purchase_date " +
                "FROM purchase_history ph JOIN products p ON ph.product_id = p.id " +
                "WHERE ph.user_id = ? ORDER BY ph.purchase_date DESC", new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            PurchaseHistory purchaseHistory = new PurchaseHistory(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getString(5)
            );
            purchaseHistoryList.add(purchaseHistory);
        }
        cursor.close();
        return purchaseHistoryList;
    }
    public void checkout(int userId) {
        List<CartItem> cartItems = getCartItems(userId);
        for (CartItem item : cartItems) {
            addPurchaseHistory(userId, item.getProductId(), item.getQuantity());
        }
        clearCart(userId);
    }

    public void addSampleGlasses() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Danh sách mắt kính mẫu với nhiều thể loại
        Product[] sampleGlasses = {
                new Product(1, "Aviator Classic", "Sunglasses", 1500000, "aviator_classic.jpg", 20),
                new Product(2, "Wayfarer", "Sunglasses", 1200000, "wayfarer.jpg", 15),
                new Product(3, "Blue Light Blocking", "Eyeglasses", 800000, "blue_light_blocking.jpg", 25),
                new Product(4, "Sport Performance", "Sports Glasses", 1800000, "sport_performance.jpg", 10),
                new Product(5, "Reading Glasses", "Eyeglasses", 500000, "reading_glasses.jpg", 30),
                new Product(6, "Clubmaster", "Sunglasses", 1300000, "clubmaster.jpg", 12),
                new Product(7, "Round Metal", "Eyeglasses", 1100000, "round_metal.jpg", 18),
                new Product(8, "Oversized Fashion", "Fashion Glasses", 1600000, "oversized_fashion.jpg", 8),
                new Product(9, "Titanium Frame", "Eyeglasses", 2000000, "titanium_frame.jpg", 5),
                new Product(10, "Polarized Sports", "Sports Glasses", 1700000, "polarized_sports.jpg", 14),
                new Product(11, "Cat Eye Luxury", "Fashion Glasses", 2200000, "cat_eye_luxury.jpg", 10),
                new Product(12, "Retro Round", "Sunglasses", 1400000, "retro_round.jpg", 16),
                new Product(13, "Cycling Shield", "Sports Glasses", 2500000, "cycling_shield.jpg", 7),
                new Product(14, "Minimalist Square", "Eyeglasses", 900000, "minimalist_square.jpg", 20),
                new Product(15, "Photochromic Smart", "Eyeglasses", 1900000, "photochromic_smart.jpg", 12)
        };

        // Thêm từng sản phẩm vào cơ sở dữ liệu
        for (Product product : sampleGlasses) {
            ContentValues values = new ContentValues();
            values.put("id", product.getId());
            values.put("name", product.getName());
            values.put("category", product.getCategory());
            values.put("price", product.getPrice());
            values.put("image", product.getImage());
            values.put("stock", product.getStock());

            // Chèn sản phẩm vào bảng products
            db.insert(TABLE_PRODUCTS, null, values);
        }

        Log.d("Database", "15 sample glasses added successfully.");
    }




}
