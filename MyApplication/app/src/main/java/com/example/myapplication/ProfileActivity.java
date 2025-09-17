package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvFullName;
    private Button btnChangePassword, btnLogout, btnHistory;
    private ImageButton btnBack, btnBackHistory;
    private Database database;
    private int userId;
    private BottomSheetDialog bottomSheetDialog;
    private RecyclerView rvPurchaseHistory;
    private PurchaseHistoryAdapter purchaseHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ View từ XML
        tvFullName = findViewById(R.id.tvFullName);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);
        btnHistory = findViewById(R.id.btnHistory);
        btnBack = findViewById(R.id.btnBack);

        // Khởi tạo database
        database = new Database(this);
        userId = getIntent().getIntExtra("user_id", -1);

        if (userId == -1) {
            finish();
            return;
        }

        loadUserInfo();

        // Xử lý sự kiện bấm nút "Change Password"
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        // Xử lý sự kiện bấm nút "Logout"
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Xử lý sự kiện bấm nút "History"
        btnHistory.setOnClickListener(v -> showPurchaseHistory());


        btnBack.setOnClickListener(v -> {

            Intent intent = new Intent(this, HomeActivity.class); // Quay lại trang chủ
            intent.putExtra("user_id", userId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    // Hàm hiển thị thông tin user
    private void loadUserInfo() {
        User user = database.getUserById(userId);
        if (user != null) {
            tvFullName.setText(user.getFullName());
        }
    }

    // Hiển thị dialog đổi mật khẩu
    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Change Password");

        View view = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        final EditText etNewPassword = view.findViewById(R.id.etNewPassword);
        final EditText etConfirmPassword = view.findViewById(R.id.etConfirmPassword);

        builder.setView(view);

        builder.setPositiveButton("Change", (dialog, which) -> {
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(ProfileActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                boolean isUpdated = database.updatePassword(userId, newPassword);
                if (isUpdated) {
                    Toast.makeText(ProfileActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to change password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    // Hiển thị lịch sử mua hàng trong BottomSheetDialog
    private void showPurchaseHistory() {
        if (bottomSheetDialog == null) {
            bottomSheetDialog = new BottomSheetDialog(this);
            View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_history, null);
            rvPurchaseHistory = sheetView.findViewById(R.id.rvHistory);

            // Cấu hình RecyclerView
            rvPurchaseHistory.setLayoutManager(new LinearLayoutManager(this));
            bottomSheetDialog.setContentView(sheetView);
        }

        // Tải dữ liệu từ database
        loadPurchaseHistory();

        // Hiển thị BottomSheetDialog
        bottomSheetDialog.show();
    }

    private void loadPurchaseHistory() {
        List<PurchaseHistory> purchaseHistoryList = database.getPurchaseHistory(userId);
        purchaseHistoryAdapter = new PurchaseHistoryAdapter(purchaseHistoryList);
        rvPurchaseHistory.setAdapter(purchaseHistoryAdapter);
    }
}
