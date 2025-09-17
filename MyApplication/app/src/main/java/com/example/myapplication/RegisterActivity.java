package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    private EditText etFullName, etNewUsername, etPhone, etNewPassword;
    private Spinner spinnerGender; // Sử dụng Spinner thay vì AutoCompleteTextView
    private TextInputLayout fullNameLayout, newUsernameLayout, phoneLayout, genderLayout, newPasswordLayout;
    private Button btnRegister;
    private Database dbHelper;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Liên kết các biến với các thành phần trong XML
        etFullName = findViewById(R.id.etFullName);
        etNewUsername = findViewById(R.id.etNewUsername);
        etPhone = findViewById(R.id.etPhone);
        spinnerGender = findViewById(R.id.spinnerGender); // Liên kết Spinner
        etNewPassword = findViewById(R.id.etNewPassword);
        btnRegister = findViewById(R.id.btnRegister);
        dbHelper = new Database(this);

        // Liên kết các TextInputLayout
        fullNameLayout = findViewById(R.id.fullNameLayout);
        newUsernameLayout = findViewById(R.id.newUsernameLayout);
        phoneLayout = findViewById(R.id.phoneLayout);
        genderLayout = findViewById(R.id.genderLayout);
        newPasswordLayout = findViewById(R.id.newPasswordLayout);

        // Thiết lập Adapter cho Spinner (Giới tính)
        String[] genders = {"Gender", "Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        // Xử lý sự kiện khi nhấn nút Đăng ký
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = etFullName.getText().toString();
                String username = etNewUsername.getText().toString();
                String phone = etPhone.getText().toString();
                String gender = spinnerGender.getSelectedItem().toString(); // Lấy giá trị từ Spinner
                String password = etNewPassword.getText().toString();

                // Reset lỗi trước khi kiểm tra
                fullNameLayout.setError(null);
                newUsernameLayout.setError(null);
                phoneLayout.setError(null);
                genderLayout.setError(null);
                newPasswordLayout.setError(null);

                // Kiểm tra các trường hợp
                if (fullName.isEmpty()) {
                    fullNameLayout.setError("Full name cannot be empty!");
                } else if (username.isEmpty()) {
                    newUsernameLayout.setError("Username cannot be empty!");
                } else if (dbHelper.checkUsername(username)) {
                    newUsernameLayout.setError("Username already exists!");
                } else if (phone.isEmpty()) {
                    phoneLayout.setError("Phone number cannot be empty!");
                } else if (gender.isEmpty()) {
                    genderLayout.setError("Gender cannot be empty!");
                } else if (password.isEmpty()) {
                    newPasswordLayout.setError("Password cannot be empty!");
                } else if (password.length() < 6) {
                    newPasswordLayout.setError("Password must be at least 6 characters!");
                } else {
                    if (dbHelper.addUser(fullName, username, phone, gender, password)) {
                        Toast.makeText(RegisterActivity.this, "Registered successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        tvLogin = findViewById(R.id.tvLogin);
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}